package com.lieblich.jon.playme.event_detail_components;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lieblich.jon.playme.R;
import com.lieblich.jon.playme.model.GameResultObject;
import com.lieblich.jon.playme.model.ReleaseDateObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jon on 12/22/2016.
 */

public class VideoGamesRecyclerAdapter extends RecyclerView.Adapter<VideoGameViewHolder> {
    private List<GameResultObject> mVideogames;
    private Intent mIntent;
    private boolean mNintendo, mXbox, mPc, mPlaystation;

    public VideoGamesRecyclerAdapter(List<GameResultObject> list) {
        mVideogames = list;
    }


    @Override
    public VideoGameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoGameViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.game_event_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final VideoGameViewHolder holder, int position) {
        final GameResultObject game = mVideogames.get(position);

        if(game.getCover() != null&&game.getCover().getUrl().length() > 0) {
            String cover = "https://images.igdb.com/igdb/image/upload/t_cover_small/"
                    +game.getCover().getCloudinary_id()+".jpg";
            Picasso.with(holder.mContext).load(cover).fit().centerInside().into(holder.mCoverArt);
        } else {
            holder.mCoverArt.setImageResource(R.drawable.ic_broken_image_black_24dp);
        }
        holder.mGameTitle.setText(game.getName());

        holder.mGameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIntent= new Intent(holder.mContext, EventDetailActivity.class);

                if(game.getScreenshots() != null) {
                    String image = game.getScreenshots().get(0).getCloudinary_id();
                    mIntent.putExtra("image", image);
                }

                consoleAssignments(mIntent, holder.getAdapterPosition());
                mIntent.putExtra("summary", game.getSummary());
                mIntent.putExtra("name", game.getName());
                mIntent.putExtra("id", game.getId());
                if(game.getCover() != null) {
                    mIntent.putExtra("cover", game.getCover().getCloudinary_id());
                }
                holder.mContext.startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideogames.size();
    }

    private void consoleAssignments(Intent intent, int position) {

        mNintendo = false;
        mXbox = false;
        mPc = false;
        mPlaystation = false;

        List<ReleaseDateObject> dates = mVideogames.get(position).getRelease_dates();

        for(int i=0;i<dates.size();i++) {
            int platform = dates.get(i).getPlatform();
            switch(platform) {
                case 4:
                case 5:
                case 18:
                case 19:
                case 21:
                case 41:
                case 130:
                    mNintendo = true;
                    break;
                case 3:
                case 6:
                case 14:
                    mPc = true;
                    break;
                case 7:
                case 8:
                case 9:
                case 38:
                case 46:
                case 48:
                    mPlaystation = true;
                    break;
                case 11:
                case 12:
                case 49:
                    mXbox = true;
                    break;
            }
        }

        ArrayList<String> consoles = new ArrayList<>();
        if(mXbox) {
            consoles.add("XBox");
        }
        if(mNintendo) {
            consoles.add("Nintendo");
        }
        if(mPlaystation) {
            consoles.add("PlayStation");
        }
        if(mPc) {
            consoles.add("PC");
        }
        if(!mNintendo&&!mPlaystation&&!mXbox&&!mPc) {
            consoles.add("Other");
        }
        intent.putExtra("platforms", consoles);
    }
}
