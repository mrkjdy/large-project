package commrkjdylarge_project.github.stepwithfriends;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>
{
    private static final int MAX_ITEMS = 100;
    private static final String TAG = "ListAdaptor";

    private ArrayList<Boolean> mFriend;
    private ArrayList<String> mUserName;
    private ArrayList<String> mUserScore;
    private static Integer userPos;
    private Context context;
    private static boolean showAdd;

    public static String toDelete = "";
    public static int deleted = 0;

    // DONE add a field that determines if an user in the leaderboard is our friend
    public ListAdapter(ArrayList<Boolean> mFriend, ArrayList<String> mUserName, ArrayList<String> mUSerScore, Context context) {
        this.mFriend = mFriend;
        this.mUserName = mUserName;
        this.mUserScore = mUSerScore;
        userPos = 0;
        this.context = context;
        this.showAdd = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        userPos++;
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        Log.d(TAG, "onBindViewHolder: called");

//        Transforms URLs into images
//        Glide.with(context)
//                .asBitmap()
//                .load(mImage.get(i))
//                .into(viewHolder.usrImage);

        viewHolder.usrName.setText(mUserName.get(i));
        viewHolder.score.setText(mUserScore.get(i));
        viewHolder.pos.setText(""+(i + 1));

        // Random pics
//        Random rand = new Random();
//        int val = rand.nextInt(5);
//
//        switch (val)
//        {
//            case 0:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_2);
//                break;
//            case 1:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_4);
//                break;
//            case 2:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_2);
//                break;
//            case 3:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_4);
//                break;
//            case 4:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_2);
//                break;
//            default:
//                viewHolder.usrImage.setImageResource(R.drawable.space_avitar_4);
//                break;
//        }
        // End

        //Log.d(TAG, "onBindViewHolder: " + showAdd);
        if (showAdd == true)
        {
            viewHolder.addFriend.setVisibility(View.VISIBLE);
            if (mFriend.get(i) == true)
            {
                viewHolder.addFriend.setImageResource(android.R.drawable.ic_delete);
                viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: clicked on: " + mUserName.get(i));
                        deleted = 0;

                        //DONE: add functionality to delete a friend here
                        toDelete = viewHolder.usrName.getText().toString();
                        //DONE: show a popup confirming to delete the friend

                        AlertDialog.Builder alt = new AlertDialog.Builder(context);
                        alt.setMessage("Do you want to delete " + toDelete + "?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Delete user from local data
                                        boolean result = ((SWFApp) context.getApplicationContext()).deleteFriend(toDelete);
                                        mFriend.remove(i);
                                        mUserName.remove(i);
                                        mUserScore.remove(i);
                                        notifyDataSetChanged();
                                        // End
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = alt.create();
                        alert.show();
                    }
                });
            }
            else
            {
                viewHolder.addFriend.setImageResource(android.R.drawable.ic_input_add);
                viewHolder.addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "onClick: clicked on: " + mUserName.get(i));

                        //DONE: add functionality to add a friend here
                        boolean result = ((SWFApp) context.getApplicationContext()).addFriend(viewHolder.usrName.getText().toString());
                        //Log.d(TAG, "onClick: Was added? " + result + "\n\n");
                        Toast.makeText(context, "Added: " + mUserName.get(i), Toast.LENGTH_SHORT).show();
                        viewHolder.addFriend.setImageResource(android.R.drawable.ic_delete);
                        mFriend.set(i, true);
                        notifyDataSetChanged();
                    }
                });
            }
        }
        else
        {
            viewHolder.addFriend.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (MAX_ITEMS < mUserName.size()) ? MAX_ITEMS : mUserName.size();
    }

    // DONE add a field that determines if an user in the leaderboard is our friend
    public void setData(ArrayList<Boolean> mFriend, ArrayList<String> mUserName, ArrayList<String> mUSerScore, boolean showAdd, Context context)
    {
        this.mFriend = mFriend;
        this.mUserName = mUserName;
        this.mUserScore = mUSerScore;
        this.context = context;
        this.showAdd = showAdd;
        notifyDataSetChanged(); // Notify that the dataset has changed, and updates the recyclerview
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView usrImage;
        TextView pos;
        TextView usrName;
        TextView score;
        ImageButton addFriend;
        ConstraintLayout entry;

        public ViewHolder(View itemView) {
            super(itemView);

            usrImage = itemView.findViewById(R.id.usr_image);
            pos = itemView.findViewById(R.id.tv_lead_pos);
            usrName = itemView.findViewById(R.id.tv_usr_name);
            score = itemView.findViewById(R.id.tv_usr_score);
            entry = itemView.findViewById(R.id.lead_entry);
            addFriend = itemView.findViewById(R.id.bt_add_friend);
        }
    }
}