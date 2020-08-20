package com.example.baptcserver.ui.crop_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.baptcserver.Adapter.MyCropListAdapter;
import com.example.baptcserver.Common.Common;
import com.example.baptcserver.Common.MySwipeHelper;
import com.example.baptcserver.EventBus.ChangeMenuClick;
import com.example.baptcserver.EventBus.ToastEvent;
import com.example.baptcserver.Model.CropModel;
import com.example.baptcserver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CropListFragment extends Fragment {

    //Image
    private static final int PICK_IMAGE_REQUEST = 1234;
    private ImageView img_crop;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private android.app.AlertDialog dialog;

    private CropListViewModel cropListViewModel;

    private List<CropModel> cropModelList;

    Unbinder unbinder;
    @BindView(R.id.recycler_crop_list)
    RecyclerView recycler_crop_list;

    LayoutAnimationController layoutAnimationController;
    MyCropListAdapter adapter;
    private Uri imageUri=null;

    @SuppressLint("FragmentLiveDataObserve")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cropListViewModel =
                ViewModelProviders.of(this).get(CropListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_crop_list, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        cropListViewModel.getMutableLiveDataCropList().observe(this, cropModels -> {
            if (cropModels != null) {
                cropModelList = cropModels;
                adapter = new MyCropListAdapter(getContext(), cropModelList);
                recycler_crop_list.setAdapter(adapter);
                recycler_crop_list.setLayoutAnimation(layoutAnimationController);
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crop_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create)
            showAddDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_crop, null);
        EditText edt_crop_name = (EditText)itemView.findViewById(R.id.edt_crop_name);
        EditText edt_crop_price = (EditText)itemView.findViewById(R.id.edt_crop_price);
        EditText edt_crop_description = (EditText)itemView.findViewById(R.id.edt_crop_description);
        img_crop = (ImageView)itemView.findViewById(R.id.img_crop_image);

        //Set data

        Glide.with(getContext()).load(R.drawable.ic_baseline_image_24).into(img_crop);

        //Set event
        img_crop.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        builder.setNegativeButton("CANCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("CREATE",((dialogInterface, i) -> {
                    CropModel updateCrop = new CropModel();
                    updateCrop.setName(edt_crop_name.getText().toString());
                    updateCrop.setDescription(edt_crop_description.getText().toString());
                    updateCrop.setPrice(TextUtils.isEmpty(edt_crop_price.getText()) ? 0 :
                            Long.parseLong(edt_crop_price.getText().toString()));
                    if(imageUri != null){
                        //Firebase storage
                        dialog.setMessage("Uploading...");
                        dialog.show();
                        String unique_name = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("image/" + unique_name);
                        imageFolder.putFile(imageUri)
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            dialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                updateCrop.setImage(uri.toString());
                                if (Common.categorySelected.getCrops() == null)
                                    Common.categorySelected.setCrops(new ArrayList<>());
                                Common.categorySelected.getCrops().add(updateCrop);
                                updateCrop(Common.categorySelected.getCrops(), Common.ACTION.CREATE);

                            });
                        }).addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                        });

                    } else {
                        if (Common.categorySelected.getCrops() == null)
                            Common.categorySelected.setCrops(new ArrayList<>());
                        Common.categorySelected.getCrops().add(updateCrop);
                        updateCrop(Common.categorySelected.getCrops(), Common.ACTION.CREATE);
                    }
                }));

        builder.setView(itemView);
        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }

    private void initViews() {
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ((AppCompatActivity) getActivity())
                .getSupportActionBar()
                .setTitle(Common.categorySelected.getName());

        recycler_crop_list.setHasFixedSize(true);
        recycler_crop_list.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_crop_list, 300) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#9b0000"),
                        pos -> {
                            if (cropModelList != null)
                                Common.selectedCrop = cropModelList.get(pos);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("DELETE")
                                    .setMessage("Do you want to delete this food")
                                    .setNegativeButton("CANCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                                    .setPositiveButton("DELETE", ((dialogInterface, i) -> {
                                        Common.categorySelected.getCrops().remove(pos);
                                        updateCrop(Common.categorySelected.getCrops(), Common.ACTION.DELETE);

                                    }));
                            AlertDialog deleteDialog = builder.create();
                            deleteDialog.show();

                        }));
                buf.add(new MyButton(getContext(), "Update", 30, 0, Color.parseColor("#560027"),
                        pos -> {
                            CropModel cropModel = adapter.getItemAtPosition(pos);
                            if (cropModel.getPositionInList() == -1) {
                                showUpdateDialog(pos, cropModel);
                            } else {
                                showUpdateDialog(cropModel.getPositionInList(), cropModel);
                            }
                        }));
            }
        };

        setHasOptionsMenu(true);
    }

    private void showUpdateDialog(int pos, CropModel cropModel) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_crop, null);
        EditText edt_crop_name = (EditText)itemView.findViewById(R.id.edt_crop_name);
        EditText edt_crop_price = (EditText)itemView.findViewById(R.id.edt_crop_price);
        EditText edt_crop_description = (EditText)itemView.findViewById(R.id.edt_crop_description);
        img_crop = (ImageView)itemView.findViewById(R.id.img_crop_image);

        //Set data
        edt_crop_name.setText(new StringBuilder("")
        .append(cropModel.getName()));
        edt_crop_price.setText(new StringBuilder("")
        .append(cropModel.getPrice()));
        edt_crop_description.setText(new StringBuilder("")
        .append(cropModel.getDescription()));

        Glide.with(getContext()).load(cropModel.getImage()).into(img_crop);

        //Set event
        img_crop.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        builder.setNegativeButton("CANCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("UPDATE",((dialogInterface, i) -> {
                    CropModel updateCrop = cropModel;
                    updateCrop.setName(edt_crop_name.getText().toString());
                    updateCrop.setDescription(edt_crop_description.getText().toString());
                    updateCrop.setPrice(TextUtils.isEmpty(edt_crop_price.getText()) ? 0 :
                            Long.parseLong(edt_crop_price.getText().toString()));
                    if(imageUri != null){
                        //Firebase storage
                        dialog.setMessage("Uploading...");
                        dialog.show();
                        String unique_name = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("image/" + unique_name);
                        imageFolder.putFile(imageUri)
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            dialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                updateCrop.setImage(uri.toString());
                                Common.categorySelected.getCrops().set(pos, updateCrop);
                                updateCrop(Common.categorySelected.getCrops(), Common.ACTION.UPDATE);
                            });
                        }).addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            dialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                        });

                    } else {
                        Common.categorySelected.getCrops().set(pos, updateCrop);
                        updateCrop(Common.categorySelected.getCrops(), Common.ACTION.UPDATE);
                    }
                }));

        builder.setView(itemView);
        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                img_crop.setImageURI(imageUri);
            }
        }
    }

    private void updateCrop(List<CropModel> crops, Common.ACTION action) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("Crops", crops);
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .updateChildren(updateData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cropListViewModel.getMutableLiveDataCropList();
                            EventBus.getDefault().postSticky(new ToastEvent(action, true));
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }
}