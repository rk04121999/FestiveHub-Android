package com.example.javapractice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class homepage extends AppCompatActivity {

    private LinearLayout eventContainer;

    private FirebaseFirestore db;

    private ImageView btnProfile;

    private LinearLayout navHome;
    private LinearLayout navEvents;
    private LinearLayout navProfile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_homepage);


//        btnProfile = findViewById(R.id.btnProfile);
//
//        btnProfile.setOnClickListener(v -> {
//
//            Intent intent = new Intent(
//                    homepage.this,
//                    ProfileActivity.class
//            );
//
//            startActivity(intent);
//        });

        eventContainer =
                findViewById(R.id.eventContainer);

        navHome = findViewById(R.id.navHome);
        navEvents = findViewById(R.id.navEvents);
        navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> {

        });

        navEvents.setOnClickListener(v -> {

            Intent intent = new Intent(
                    homepage.this,
                    EventsActivity.class
            );

            startActivity(intent);
        });

//        navEvents.setOnClickListener(v -> {
//
//            Intent intent = new Intent(
//                    homepage.this,
//                    EventManagementActivity.class
//            );
//
//            startActivity(intent);
//        });

        navProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    homepage.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });


        db = FirebaseFirestore.getInstance();


        loadEvents();


        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }


    private void loadEvents() {

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {


                    eventContainer.removeAllViews();



                    for (DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        Event event =
                                document.toObject(Event.class);

                        if (event != null) {

                            addEventCard(event);
                        }
                    }


                    if (queryDocumentSnapshots.isEmpty()) {

                        Toast.makeText(
                                homepage.this,
                                "No events found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            homepage.this,
                            "Error loading events: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }


    private void addEventCard(Event event) {

        View eventView =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.event_item,
                                eventContainer,
                                false
                        );


        ImageView ivEventImage =
                eventView.findViewById(
                        R.id.ivEventImage
                );

        TextView tvEventName =
                eventView.findViewById(
                        R.id.tvEventName
                );

        TextView tvEventLocation =
                eventView.findViewById(
                        R.id.tvEventLocation
                );

        ImageButton btnEventArrow =
                eventView.findViewById(
                        R.id.btnEventArrow
                );



        tvEventName.setText(
                event.getEventName()
        );


        tvEventLocation.setText(
                event.getEventPlace()
        );


        int imageResource =
                getResources().getIdentifier(
                        event.getEventImage(),
                        "drawable",
                        getPackageName()
                );


        if (imageResource != 0) {

            ivEventImage.setImageResource(
                    imageResource
            );

        } else {

            ivEventImage.setImageResource(
                    R.drawable.aisummit
            );
        }

        btnEventArrow.setOnClickListener(v -> {

            Intent intent = new Intent(
                    homepage.this,
                    eventdetailactivity.class
            );


            intent.putExtra("eventName", event.getEventName());
            intent.putExtra("eventDate", event.getEventDate());
            intent.putExtra("eventTime", event.getEventTime());
            intent.putExtra("eventPlace", event.getEventPlace());
            intent.putExtra("eventAddress", event.getEventAddress());
            intent.putExtra("ticketPrice", event.getTicketPrice());
            intent.putExtra("eventDescription", event.getEventDescription());

//            int imageResource = getResources().getIdentifier(
//                    event.getEventImage(),
//                    "drawable",
//                    getPackageName()
//            );

//            intent.putExtra("eventImage", imageResource);

            startActivity(intent);
        });


//        btnEventArrow.setOnClickListener(v -> {
//
//            Intent intent = new Intent(
//                    homepage.this,
//                    eventdetailactivity.class
//            );
//
//
//            intent.putExtra(
//                    "eventName",
//                    event.getEventName()
//            );
//
//            intent.putExtra(
//                    "eventDate",
//                    event.getEventDate()
//            );
//
//            intent.putExtra(
//                    "eventTime",
//                    event.getEventTime()
//            );
//
//            intent.putExtra(
//                    "eventPlace",
//                    event.getEventPlace()
//            );
//
//            intent.putExtra(
//                    "eventAddress",
//                    event.getEventAddress()
//            );
//
//            intent.putExtra(
//                    "ticketPrice",
//                    event.getTicketPrice()
//            );
//
//            intent.putExtra(
//                    "eventDescription",
//                    event.getEventDescription()
//
//            );
//
//            int imageResource = getResources().getIdentifier(
//                    event.getEventImage(),
//                    "drawable",
//                    getPackageName()
//            );
//
//            intent.putExtra(
//                    "eventImage",
//                    imageResource
//            );
//
//
//            startActivity(intent);
//        });


        eventContainer.addView(eventView);
    }


    @Override
    protected void onResume() {

        super.onResume();

        if (db != null &&
                eventContainer != null) {

            loadEvents();
        }
    }
}