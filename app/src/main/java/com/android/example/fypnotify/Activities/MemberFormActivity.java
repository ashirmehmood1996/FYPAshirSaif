package com.android.example.fypnotify.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.example.fypnotify.Models.MemberModel;
import com.android.example.fypnotify.R;


public class MemberFormActivity extends AppCompatActivity {
    private EditText nameEditText, numberEditText, membertypeEditText;
    private Button submitButton, selectFromDeviceButton;
    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        nameEditText = findViewById(R.id.et_member_form_name);
        numberEditText = findViewById(R.id.et_member_form_number);
        membertypeEditText = findViewById(R.id.et_member_form_member_type);
        submitButton = findViewById(R.id.bt_add_record_to_database);
        selectFromDeviceButton = findViewById(R.id.bt_select_from_device);
        selectFromDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickContact();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemberFormActivity.this, MainActivity.class);
                MemberModel member = new MemberModel(0, nameEditText.getText().toString().trim(),//given id zero but it will not be used any further
                        numberEditText.getText().toString().trim(),
                        membertypeEditText.getText().toString().trim());
                intent.putExtra("member", member);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // from developer.android
    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = resultIntent.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};


                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using <code><a href="/reference/android/content/CursorLoader.html">CursorLoader</a></code> to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int columnNunmberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameColoumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String number = cursor.getString(columnNunmberIndex);
                String name = cursor.getString(nameColoumnIndex);

                numberEditText.setText(number + "");
                nameEditText.setText(name + "");


                // Do something with the phone number...
            }
        }
    }
}
