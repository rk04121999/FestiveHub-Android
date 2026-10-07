package com.example.javapractice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegisteredUserAdapter
        extends RecyclerView.Adapter<RegisteredUserAdapter.UserViewHolder> {

    private final Context context;
    private final List<RegisteredUser> userList;

    public  RegisteredUserAdapter(
            Context context,
            List<RegisteredUser> userList) {

        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.registered_user_item,
                parent,
                false
        );

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position) {

        RegisteredUser user = userList.get(position);

        holder.tvUserEmail.setText(user.getUserEmail());

        holder.tvUserId.setText(
                context.getString(
                        R.string.user_id,
                        String.valueOf(user.getUserId())
                )
        );

        holder.tvRegistrationDate.setText(
                context.getString(
                        R.string.registered_for,
                        user.getEventName()
                )
        );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvUserEmail;
        TextView tvUserId;
        TextView tvRegistrationDate;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserEmail = itemView.findViewById(
                    R.id.tvUserEmail
            );

            tvUserId = itemView.findViewById(
                    R.id.tvUserId
            );

            tvRegistrationDate = itemView.findViewById(
                    R.id.tvRegistrationDate
            );
        }
    }
}

