package com.grig.mytraining.ui.home.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grig.mytraining.PermissionUtils;
import com.grig.mytraining.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class SettingsActivity extends AppCompatActivity {
    byte isExport = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.exportDataButton).setOnClickListener(view -> exportDB()
        );
        findViewById(R.id.importDataButton).setOnClickListener(view -> importDB());

//        findViewById(R.id.shareDB).setOnClickListener(v -> {
////            Intent intent = new Intent(Intent.ACTION_SEND);
////            intent.setType("storage/emulated/0/MyTrainingBackupDB/trainingDBBackup");
////            startActivity(intent);
////            Intent shareIntent = new Intent();
////            shareIntent.setAction(Intent.ACTION_SEND);
////            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
////            shareIntent.setType("image/jpeg");
////            startActivity(Intent.createChooser(shareIntent, null));
////            sendIntent.setType("*/*");
////
////            Intent shareIntent = Intent.createChooser(sendIntent, null);
////            startActivity(shareIntent);
////            Intent shareIntent = new Intent();
////            shareIntent.setAction(Intent.ACTION_SEND);
////            Toast.makeText(this, String.valueOf(new File("storage/emulated/0/MyTrainingBackupDB/trainingDBBackup").exists()), Toast.LENGTH_SHORT).show();
////            shareIntent.putExtra(Intent.EXTRA, "storage/emulated/0/MyTrainingBackupDB/trainingDBBackup");
////            shareIntent.setType("*/*");
////            startActivity(Intent.createChooser(shareIntent, null)
//        }));


//            Intent intent = new Intent("com.grig.mytraining.ACTION_RETURN_FILE");
//            intent.setType("application/pdf");
//            startActivity(intent);
//            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
////            File fileWithinMyDir = new File(Environment.getExternalStorageDirectory() + "/MyTrainingBackupDB/trainingDBbackup");
//            File fileWithinMyDir = new File("storage/emulated/0/MyTrainingBackupDB", "trainingDBBackup");
//            if(fileWithinMyDir.exists()) {
//                intentShareFile.setType("application/pdf");
//                    Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileProvider", fileWithinMyDir);
//                    intentShareFile.putExtra(Intent.EXTRA_STREAM, contentUri);
//                    intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intentShareFile.putExtra (Intent.EXTRA_SUBJECT, "Отправить файл ...");
//                intentShareFile.putExtra (Intent.EXTRA_TEXT, "Отправить файл ...");
//                startActivity(Intent.createChooser(intentShareFile, "Share File"));
//            } else System.out.println(fileWithinMyDir);
//        });

        findViewById(R.id.delExerciseButton).setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, DeleteExerciseDialog.class);
            startActivity(intent);
        });

        findViewById(R.id.replaceExerciseButton).setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, DialogReplaceExercise.class);
            startActivity(intent);
        });

        findViewById(R.id.goBackButtonFromSettings).setOnClickListener(view -> finish());

        findViewById(R.id.donate).setOnClickListener(v -> {
                    Toast.makeText(SettingsActivity.this,
                            "Номер телефона: +79251199476 скопирован!", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", "+79251199467");
                    clipboard.setPrimaryClip(clip);
                }
        );
        findViewById(R.id.aboutApp).setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this, DialogAboutApp.class);
            startActivity(intent);
        });
    }

    public String DATABASE_NAME = "trainingDB";

    public void exportDB() {
        File backupFolder = new File(Environment.getExternalStorageDirectory(), "MyTrainingBackupDB");
        if (PermissionUtils.hasPermissions(getApplicationContext())) {
            if (backupFolder.canWrite()) {
                File currentDB = getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsoluteFile();
                File copyDB = new File(backupFolder, DATABASE_NAME + "Backup");
                if (currentDB.exists()) {
                    FileChannel inputStream;
                    FileChannel outputStream;
                    try {
                        inputStream = new FileInputStream(currentDB).getChannel();
                        outputStream = new FileOutputStream(copyDB).getChannel();
                        outputStream.transferFrom(inputStream, 0, inputStream.size());
//                            FileUtils.copy(new FileInputStream(currentDB), new FileOutputStream(copyDB));
                        Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show();
                    } catch (IOException ignored) {
                    }
                } else {
                    Toast.makeText(this, "База данных не существует!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (backupFolder.mkdir()) {
                    Toast.makeText(this, "Создание каталога...", Toast.LENGTH_SHORT).show();
                    exportDB();
                } else
                    Toast.makeText(this, "Невозможно создать каталог!", Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionUtils.requestPermissions(SettingsActivity.this, 101);
            isExport = 1;

        }
    }

    public void importDB() {
        File backupFolder = new File(Environment.getExternalStorageDirectory(), "MyTrainingBackupDB");

        if (PermissionUtils.hasPermissions(getApplicationContext())) {
            if (backupFolder.canWrite()) {
                File currentDB = getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsoluteFile();
                File copyDB = new File(backupFolder, DATABASE_NAME + "Backup");

                if (copyDB.exists()) {
                    FileChannel inputStream;
                    FileChannel outputStream;
                    try {
                        inputStream = new FileInputStream(copyDB).getChannel();
                        outputStream = new FileOutputStream(currentDB).getChannel();
                        outputStream.transferFrom(inputStream, 0, inputStream.size());
                        Toast.makeText(this, "Успешно!", Toast.LENGTH_SHORT).show();
                    } catch (IOException ignored) {
                    }
                } else {
                    Toast.makeText(this, "Нет резервной базу данных!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (backupFolder.mkdir()) {
                    Toast.makeText(this, "Создание каталога...", Toast.LENGTH_SHORT).show();
                    importDB();
                } else
                    Toast.makeText(this, "Невозможно создать каталог!", Toast.LENGTH_SHORT).show();
            }
        } else {
            PermissionUtils.requestPermissions(SettingsActivity.this, 101);
            isExport = 2;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isExport == 1) {
            if (PermissionUtils.hasPermissions(getApplicationContext())) {
                Toast.makeText(this, "Разрешение получено!", Toast.LENGTH_SHORT).show();
                exportDB();
            } else
                Toast.makeText(this, "Разрешение не получено!", Toast.LENGTH_SHORT).show();
        }
        if (isExport == 2) {
            if (PermissionUtils.hasPermissions(getApplicationContext())) {
                Toast.makeText(this, "Разрешение получено!", Toast.LENGTH_SHORT).show();
                importDB();
            } else
                Toast.makeText(this, "Разрешение не получено!", Toast.LENGTH_SHORT).show();
        }
    }
}