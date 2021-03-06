package com.android.example.fypnotify.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.example.fypnotify.Adapters.SelectedImagesAdapter;
import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.Models.NotificationModel;
import com.android.example.fypnotify.R;
import com.android.example.fypnotify.Database.DatabaseContract;
import com.android.example.fypnotify.Database.MembersDatabaseHelper;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateNotificationActivity extends AppCompatActivity {
    public static final int RC_SELECT_CONTACTS = 1000;
    private static final int PICK_IMAGE = 11;
    static final int REQUEST_IMAGE_CAPTURE = 101;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetLinearLayout;
    private View blurrView;
    private EditText titleEditText;
    //private RTEditText messageRtEditText;
    private EditText messageEditText;

    private Button generatePdfButton;
    private ProgressBar progressBar;
    //recycler view related
    private RecyclerView selectedImagesHolderRecyclerView;
    private RelativeLayout pdfHolderRelativeLayout;
    private RelativeLayout createNotificationBottomViewHolderRelativeLayout;
    private SelectedImagesAdapter selectedImagesAdapter;
    private ArrayList<Uri> selectedImagesUriArrayList;
    //ImageView imageView;
    //Uri imageUri = null;

    //private RTManager rtManager;

    private final int RC_SCAN_USING_CAMERA = 99;
    private final int RC_SCAN_USING_GALLERY = 98;
    private File pdfFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_notification);

        messageEditText = findViewById(R.id.et_message_text);


        bottomSheetLinearLayout = findViewById(R.id.bottom_sheet_send_notificaton);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLinearLayout);
        blurrView = findViewById(R.id.v_create_notification_blur);
        blurrView.setVisibility(View.GONE);
        titleEditText = findViewById(R.id.et_title);
        generatePdfButton = findViewById(R.id.bt_create_notification_generate_pdf);
        progressBar = findViewById(R.id.pb_create_notification);
        createNotificationBottomViewHolderRelativeLayout = findViewById(R.id.rl_create_notification_bottom_view_holder);
        pdfHolderRelativeLayout = findViewById(R.id.rl_create_notification_pdf_holder);
        pdfHolderRelativeLayout.setVisibility(View.GONE);
        //imageView = findViewById(R.id.iv_create_notification_image);
        selectedImagesHolderRecyclerView = findViewById(R.id.rv_create_notification_selected_images_holder);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        selectedImagesHolderRecyclerView.setLayoutManager(linearLayoutManager);
        selectedImagesUriArrayList = new ArrayList<>();
        selectedImagesAdapter = new SelectedImagesAdapter(selectedImagesUriArrayList, this);
        selectedImagesHolderRecyclerView.setAdapter(selectedImagesAdapter);

        setBottomSheetCallBacks();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        generatePdfButton.setVisibility(View.GONE);
        generatePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GeneratePDFAsyncTask().execute();
            }
        });
    }

    private void setBottomSheetCallBacks() {

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {

                    case BottomSheetBehavior.STATE_HIDDEN:

                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:

                        break;

                    case BottomSheetBehavior.STATE_COLLAPSED:

                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:

                        break;

                    case BottomSheetBehavior.STATE_SETTLING:

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }


    private void sendViaEmail(ArrayList<MemberModel> selectedContatcs) {
        String uriCVS;//this will be null for  simple text mail
        ArrayList<String> recipientsArrayList = new ArrayList<>();
        String[] recipientsArray = null;

        for (MemberModel member : selectedContatcs) {
            String curruntMemberEmail = member.getEmail();
            if (curruntMemberEmail != null && !curruntMemberEmail.isEmpty()) {
                recipientsArrayList.add(curruntMemberEmail);
            }
        }
        if (recipientsArrayList.size() > 0) {
            recipientsArray = new String[recipientsArrayList.size()];
            recipientsArray = recipientsArrayList.toArray(recipientsArray);

        } else {
            Toast.makeText(this, "none of the contacts has an email address", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "and using hard coded emails ", Toast.LENGTH_SHORT).show();
            recipientsArray = new String[]{"ashirmehmood1996@gmail.com"};
            //return; // TODO: 3/9/2019 later uncomment return when we get the emails dynamically
        }


        String subject = titleEditText.getText().toString();
        String message = messageEditText.getText().toString().trim();


        // todo future work this link was used there is more infromation for api > 24
        // https://stackoverflow.com/questions/15577438/how-can-i-share-multiple-files-via-an-intent/15577579

        if (selectedImagesUriArrayList != null && selectedImagesUriArrayList.size() > 0 && selectedImagesUriArrayList.get(0) != null) {
            uriCVS = "";
            for (int i = 0; i < selectedImagesUriArrayList.size(); i++) {

                if (!((i + 1) == selectedImagesUriArrayList.size())) //if its not the last index
                    uriCVS = uriCVS + selectedImagesUriArrayList.get(0).toString() + ",";
                else//if its the last index then donot insert comma in the end
                    uriCVS = uriCVS + selectedImagesUriArrayList.get(0).toString();
            }

            TextUtils.join(",", selectedImagesUriArrayList);
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            //intent.setData(Uri.parse("mailto:")); // only email apps should handle this not working either combined with type or alone
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_EMAIL, recipientsArray);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            // intent.putExtra(Intent.EXTRA_STREAM, selectedImagesUriArrayList.get(0));

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedImagesUriArrayList);

            //intent.setType("message/rfc822");
            if (intent.resolveActivity(getPackageManager()) != null) {
                //    startActivity(intent);
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            } else {
                Toast.makeText(this, "no application found to send this mail", Toast.LENGTH_SHORT).show();

            }

            //intent.setPackage("com.whatsapp");

        } else if (pdfFile != null) {
            uriCVS = "";
            // the create_notification
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, recipientsArray);
            //intent.setType("application/pdf");
            Uri pdfUri = Uri.fromFile(pdfFile);
            uriCVS = pdfUri.toString();
            intent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            //intent.setType("message/rfc822");
            if (intent.resolveActivity(getPackageManager()) != null) {
                //    startActivity(intent);
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }

        } else {
            uriCVS = null;
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, recipientsArray);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            //intent.setType("message/rfc822");
            if (intent.resolveActivity(getPackageManager()) != null) {
                //    startActivity(intent);
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
            //intent.setType("application/pdf");
        }


        //// TODO: 2/18/2019 uncomment later when we have data to store
        //member id is not used
        String type;
        if (uriCVS == null) {
            type = "text";
        } else {
            type = "multimedia";
        }
        NotificationModel notification = new NotificationModel(0, titleEditText.getText().toString(), messageEditText.getText().toString().trim(), "" + Calendar.getInstance().getTimeInMillis(),
                "" + TextUtils.join(",", recipientsArray), type, uriCVS
        );
        writeSentNotificationToDatabase(notification);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CreateNotificationActivity.super.onBackPressed();
                    }
                });
            }
        }, 300);
    }


    //allow image to pdf convertion option of either images or pdf or pdfs and keep the limit below 25 mb

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void sendSmsToTheMemebers(ArrayList<MemberModel> selectedContacts) {
        SmsManager smsManager = SmsManager.getDefault();
        String recievers = "";

        for (MemberModel currentMember : selectedContacts) {
            String number = currentMember.getPhoneNumber();
            smsManager.sendTextMessage(number, null, "Title:" + titleEditText.getText().toString().trim() +
                    "\n" + messageEditText.getText().toString().trim(), null, null);
            recievers = recievers + number + ", ";
        }
        //member id is not used
        NotificationModel notification = new NotificationModel(0, titleEditText.getText().toString().trim(), "" + messageEditText.getText().toString().trim(), "" + Calendar.getInstance().getTimeInMillis(),
                "" + recievers, "text", null
        );
        writeSentNotificationToDatabase(notification);
        Toast.makeText(getApplicationContext(), "Sms sent to \n" + recievers, Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CreateNotificationActivity.super.onBackPressed();
                    }
                });
            }
        }, 300);

    }

    private synchronized void writeSentNotificationToDatabase(NotificationModel notification) {

        MembersDatabaseHelper historyDataBaseHelper = new MembersDatabaseHelper(this);
        SQLiteDatabase dbWrite = historyDataBaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_TITLE, notification.getTitle());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_MESSAGE, notification.getMessage());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_TIME_STAMP, notification.getTimeStamp());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_RECIEVERS, notification.getRecievers());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_TYPE, notification.getType());
        contentValues.put(DatabaseContract.NotificationsEntry.COLOUMN_URI_LIST, notification.getUriCSV());

        long id = dbWrite.insert(DatabaseContract.NotificationsEntry.TABLE_NAME,
                null,
                contentValues);
        System.out.println("id = " + id);


//implement the query to get the recipients and make them in groups on the basis of types

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_notification, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_create_notification_attach:

                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.document_picking_options, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(linearLayout);

                builder.setTitle("Get Picture");
                AlertDialog dialog = builder.create();
                linearLayout.findViewById(R.id.ll_dpo_gallery).setOnClickListener(view -> {

                    int preference = ScanConstants.OPEN_MEDIA;
                    Intent intent = new Intent(CreateNotificationActivity.this, ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                    startActivityForResult(intent, RC_SCAN_USING_GALLERY);


                    dialog.dismiss();

                });
                linearLayout.findViewById(R.id.ll_dpo_camera).setOnClickListener(view -> {

                    int preference = ScanConstants.OPEN_CAMERA;
                    Intent intent = new Intent(CreateNotificationActivity.this, ScanActivity.class);
                    intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
                    startActivityForResult(intent, RC_SCAN_USING_CAMERA);
                    dialog.dismiss();
                });
                dialog.show();


                break;
            case R.id.nav_create_notification_send:

                sendNotification();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendNotification() {

        // TODO: 5/9/2019  later use custom view for that purpose

        blurrView.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("send to Contacts", (dialog, which) -> {
            Intent i = new Intent(this, ContactsSelect.class);
            i.putExtra("get contact", true); // added by saif
            startActivityForResult(i, RC_SELECT_CONTACTS);

        }).setNegativeButton("send to Goups", (dialog, which) -> {
            Intent intent = new Intent(this, GroupMembers.class);
            intent.putExtra("selectionForSend", true);
            startActivityForResult(intent, RC_SELECT_CONTACTS);

        }).setNeutralButton("cancel", (dialog, which) -> {
            dialog.dismiss();
            blurrView.setVisibility(View.GONE);

        }).setCancelable(false).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Uri imageUri = data.getData();
                    // this.imageUri = imageUri;
                    selectedImagesHolderRecyclerView.setVisibility(View.VISIBLE);
                    generatePdfButton.setVisibility(View.VISIBLE);
                    createNotificationBottomViewHolderRelativeLayout.setVisibility(View.VISIBLE);

                    selectedImagesUriArrayList.add(imageUri);
                    selectedImagesAdapter.notifyDataSetChanged();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
                //handle the cancels
            }
        } else if (requestCode == RC_SELECT_CONTACTS) {
            if (resultCode == RESULT_OK) {

                blurrView.setVisibility(View.VISIBLE);

                final ArrayList<MemberModel> selectedContatcs = (ArrayList<MemberModel>) data.getSerializableExtra("members_contacts");

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_notification_by_mail)
                        .setOnClickListener(view -> sendViaEmail(selectedContatcs));

                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_notification_by_sms)
                        .setOnClickListener(view -> {
                            sendSmsToTheMemebers(selectedContatcs);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        });

                bottomSheetLinearLayout.findViewById(R.id.bt_bs_send_cancel).setOnClickListener(view -> {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    selectedContatcs.clear();
                    blurrView.setVisibility(View.GONE);
                });


            } else if (resultCode == RESULT_CANCELED) {
                blurrView.setVisibility(View.GONE);

            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {

                Bundle extras = data.getExtras();
                // TODO: 2/20/2019
                Bitmap imageBitmap = (Bitmap) extras.get("data");


                selectedImagesHolderRecyclerView.setVisibility(View.VISIBLE);
                generatePdfButton.setVisibility(View.VISIBLE);
                createNotificationBottomViewHolderRelativeLayout.setVisibility(View.VISIBLE);
                selectedImagesUriArrayList.add(getImageUri(this, imageBitmap));
                selectedImagesAdapter.notifyDataSetChanged();

            }


        } else if (requestCode == RC_SCAN_USING_CAMERA) {

            if (resultCode == RESULT_OK) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);


                selectedImagesHolderRecyclerView.setVisibility(View.VISIBLE);
                createNotificationBottomViewHolderRelativeLayout.setVisibility(View.VISIBLE);
                selectedImagesUriArrayList.add(uri);
                selectedImagesAdapter.notifyDataSetChanged();
                generatePdfButton.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == RC_SCAN_USING_GALLERY) {

            if (resultCode == RESULT_OK) {
                Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);


                selectedImagesHolderRecyclerView.setVisibility(View.VISIBLE);
                generatePdfButton.setVisibility(View.VISIBLE);
                createNotificationBottomViewHolderRelativeLayout.setVisibility(View.VISIBLE);
                selectedImagesUriArrayList.add(uri);
                selectedImagesAdapter.notifyDataSetChanged();
            }
        }

    }

    private File generatePdf() throws IOException {
        File pdfFile = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            if (selectedImagesUriArrayList.size() > 0) {
                PdfDocument pdfDocument = new PdfDocument();
                for (int i = 0; i < selectedImagesUriArrayList.size(); i++) {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImagesUriArrayList.get(i));
                    //generating A4 size page
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(/*bitmap.getWidth()*/595, /*bitmap.getHeight()*/842, 1).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    canvas.drawPaint(paint);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 595, 842, true);
                    paint.setColor(Color.BLUE);
                    canvas.drawBitmap(bitmap, 0, 4, null);
                    pdfDocument.finishPage(page);

                    //temp
                    File root = new File(Environment.getExternalStorageDirectory(), "FYPPdfs");
                    if (!root.exists()) {
                        root.mkdir();
                    }
                    File file1 = new File(root, "notification" + i + ".jpeg");
                    FileOutputStream fileOutputStream1 = new FileOutputStream(file1);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bitmap.recycle();
                    byte[] data = baos.toByteArray();
                    fileOutputStream1.write(data);

                }

                //now save the pdf file
                //first we create the folder if it doesnt exist
                File root = new File(Environment.getExternalStorageDirectory(), "FYPPdfs");
                if (!root.exists()) {
                    root.mkdir();
                }
                // now we create the file name
                //todo later get the name at run time if deemed necessary or generate randomly
                File file = new File(root, "notification.pdf");

                FileOutputStream fileOutputStream = new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();
                pdfFile = file;
                //fileOutputStream.write();
            }

        } else {
            Toast.makeText(this, "this functionality is only supported in android kit-kat or higher versions." +
                    "you can still send images directly ", Toast.LENGTH_LONG).show();
        }
        return pdfFile;

    }


    private class GeneratePDFAsyncTask extends AsyncTask<Void, Void, File> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //todo show progress bar
        }

        @Override
        protected File doInBackground(Void... voids) {
            try {
                return generatePdf();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            progressBar.setVisibility(View.GONE);
            if (file != null) {
                pdfFile = file;
                selectedImagesUriArrayList.clear();
                selectedImagesAdapter.notifyDataSetChanged();
                pdfHolderRelativeLayout.setVisibility(View.VISIBLE);
                pdfHolderRelativeLayout.findViewById(R.id.ib_selected_pdf_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(CreateNotificationActivity.this).setTitle("remove").setMessage("tap remove tp coninue..")
                                .setPositiveButton("remove", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        file.delete();
                                        pdfHolderRelativeLayout.setVisibility(View.GONE);
                                    }
                                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

                    }
                });
                //todo display the pdf file
                Toast.makeText(CreateNotificationActivity.this, "pdf created", Toast.LENGTH_SHORT).show();
                generatePdfButton.setVisibility(View.GONE);

                //todo must for now leaving the situation for best case later handle the additional request from userr toadd further pics and when there is already a odf generated
            }


        }
    }
}




