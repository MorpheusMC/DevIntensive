package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.*;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.security.acl.AclNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private int mCurrentEditMode = 0;

    private ImageView mCallImg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNawigationDrawer;
    private FloatingActionButton mFab;
    private RelativeLayout mProfilePlaceholder;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private AppBarLayout mAppBarLayout;
    private ImageView mProfileImage;

    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserBio;

    private List<EditText> mUserInfo;

    private AppBarLayout.LayoutParams mAppBarParams = null;
    private File mPhotoFile = null;
    private Uri mSelectedImage = null;
    private String mPhoneNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();
        mCallImg = (ImageView) findViewById(R.id.call_img);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNawigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mUserPhone = (EditText) findViewById(R.id.phone_et);
        mUserMail = (EditText) findViewById(R.id.email_et);
        mUserVk = (EditText) findViewById(R.id.vk_et);
        mUserGit = (EditText) findViewById(R.id.git_et);
        mUserBio = (EditText) findViewById(R.id.about_et);
        mProfilePlaceholder = (RelativeLayout) findViewById(R.id.profile_placeholder);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mProfileImage = (ImageView) findViewById(R.id.user_photo_img);


        mUserInfo = new ArrayList<>();
        mUserInfo.add(mUserPhone);
        mUserInfo.add(mUserMail);
        mUserInfo.add(mUserVk);
        mUserInfo.add(mUserGit);
        mUserInfo.add(mUserBio);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto) //// TODO: 02.07.16 сделать плейсхолдер + трансформ  + кроп
                .into(mProfileImage);

        List<String> test = mDataManager.getPreferencesManager().loadUserProfileData();
        if (savedInstanceState == null) {

        } else {
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNawigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        saveUserInfoValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (mCurrentEditMode == 0) {
                    changeEditMode(1);
                    mCurrentEditMode = 1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode = 0;
                }

                break;

            case R.id.profile_placeholder:
                //// TODO: 02.07.16 сделать выбор откуда загружать фото
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;

            case R.id.call_img:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.CALL_PHONE

                    }, ConstantManager.CALL_REQUEST_PERMISSION_CODE);

                    Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                            .setAction("Разрешить", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openApplicationSettings();
                                }
                            }).show();


                } else {
                    mPhoneNo = mUserPhone.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    try {
                        if (mPhoneNo.length() != 0 || !mPhoneNo.equals("null")) {
                            intent.setData(Uri.parse("tel:" + mPhoneNo));
                            startActivity(intent);

                        } else {
                            showSnackbar("Номера не существует");
                        }
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        //// TODO: 02.07.16 обработать ошибку

                    }
                }
                break;

            case R.id.email_img:
                sendEmail();
                break;

            case R.id.buttonVk:
                followVk();
                break;

            case R.id.buttonGit:
                followGit();
                break;
        }
    }

    private void sendEmail() {
        String email = mUserMail.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        try {
            if (email.length() != 0 || !email.equals("null")) {

                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, email);
//                        intent.setType("message/rfc822");
//                        startActivity(intent);
                startActivity(Intent.createChooser(intent, "Выберите email клиент"));

            } else {
                showSnackbar("Номера не существует");
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //// TODO: 02.07.16 обработать ошибку
            showSnackbar("мыло");
        }
    }

    private void followVk() {
        String vk = mUserVk.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + vk));
        try {
            if (vk.length() != 0) {
                startActivity(intent);
            } else {
                showSnackbar("ВК не существует");
            }

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //// TODO: 02.07.16 обработать ошибку
            showSnackbar("вконтакте");
        }
    }

    private void followGit() {
        String git = mUserGit.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + git));
        try {
            if (git.length() != 0) {
                startActivity(intent);
            } else {
                showSnackbar("GIT не существует");
            }

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //// TODO: 02.07.16 обработать ошибку
            showSnackbar("GIT");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);

    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mAppBarParams = (AppBarLayout.LayoutParams) mCollapsingToolbar.getLayoutParams();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNawigationDrawer.closeDrawer(GravityCompat.START);

                return false;
            }
        });

    }

    /**
     * Получение резульатов из другой Активити (из Камеры или Галереи)
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode == RESULT_OK && data != null) {
                    mSelectedImage = data.getData();

                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode == RESULT_OK && mPhotoFile != null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
                break;
        }
    }


    @Override
    public void showProgress() {
        super.showProgress();
    }

    /**
     * переключает режим редактирования
     *
     * @param mode 1 режим редактирования, 0 режим просмотра
     */

    private void changeEditMode(int mode) {
        if (mode == 1) {

            mFab.setImageResource(R.drawable.ic_done_24dp);
            for (EditText userValue : mUserInfo) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);

                showProfilePlaceholder();
                lockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_24dp);
            for (EditText userValue : mUserInfo) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);

                hideProfilePlaceholder();
                unlockToolbar();
                mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));

                saveUserInfoValue();

            }
        }

    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();
        for (int i = 0; i < userData.size(); i++) {
            mUserInfo.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldView : mUserInfo) {
            userData.add(userFieldView.getText().toString());
        }
        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void loadPhotoFromGallery() {
        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(takeGalleryIntent, getString(R.string.user_profile_chose_message)), ConstantManager.REQUEST_GALLERY_PICTURE);

    }

    private void loadPhotoFromCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                //// TODO: 02.07.16 обработать ошибку
            }

            if (mPhotoFile != null) {
                //// TODO: 02.07.16 передать фотофайл в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, "Для корректной работы необходимо дать требуемые разрешения", Snackbar.LENGTH_LONG)
                    .setAction("Разрешить", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }

    // Обработка разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 02.07.16 тут обрабатываем разрешение (разрешение получено)
                // например вывести сообщение или обработать какой то логикой если нужно
            }

            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 02.07.16 тут обрабатываем разрешение (разрешение получено)
                // например вывести сообщение или обработать какой то логикой если нужно

            }
        }
        if (requestCode == ConstantManager.CALL_REQUEST_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 02.07.16 тут обрабатываем разрешение (разрешение получено)
                // например вывести сообщение или обработать какой то логикой если нужно
                showSnackbar("Разрешение на звонки получено");
            }
        }
    }

    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    // Заблокировать тулбар
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    // разблокировать Тулбар
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    // Диалог контекстного меню хидера
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItem = {getString(R.string.user_profile_dialog_gallery), getString(R.string.user_profile_dialog_camera), getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.user_profile_dialog_title);
                builder.setItems(selectItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                //// TODO: 02.07.16 загрузить из галереи
                                loadPhotoFromGallery();
                                //showSnackbar("загрузить из галереи");
                                break;
                            case 1:
                                //// TODO: 02.07.16 загрузить из камеры
                                loadPhotoFromCamera();
                                //showSnackbar("загрузить из камеры");
                                break;
                            case 2:
                                //// TODO: 02.07.16 отмена
                                dialogInterface.cancel();
                                //showSnackbar("отмена");
                                break;
                        }
                    }
                });
                return builder.create();
            default:
                return null;
        }
    }

    // Создать файл картинки из камеры
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues value = new ContentValues();
        value.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        value.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
        return image;
    }

    // вставить картинку профиля
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        //// TODO: 02.07.16 сделать плейсхолдер + трансформ  + кроп
        mDataManager.getPreferencesManager().SaveUserPhoto(selectedImage);
    }

    // Открыть настройки для установки разрешений
    public void openApplicationSettings() {
        Intent appSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingIntent, ConstantManager.PERMISSION_REQUEST_SETTING_CODE);
    }

}
