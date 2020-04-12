package com.example.playlisttest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.playlisttest.databinding.FragmentMusicListBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MusicListFragment extends Fragment implements CustomAdapter.dataListener {

    FragmentMusicListBinding binding;
    CustomAdapter customAdapter;
    private static final int MY_PERMISSON_REQUEST = 1;
    List<Songs> songsList = new ArrayList<>();
    Songs songs;
    MediaPlayer mediaPlayer;
    String search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_music_list, container, false);


        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSON_REQUEST);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSON_REQUEST);
            }

        } else {
            doStuff();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.recycleView.setLayoutManager(linearLayoutManager);
        customAdapter = new CustomAdapter(songsList, getContext(), this);
        binding.recycleView.setAdapter(customAdapter);

        setHasOptionsMenu(true);
        ((AppCompatActivity)getActivity()).setSupportActionBar(binding.toolbar);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(
             Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    

    public void doStuff() {


        ContentResolver contentResolver = getActivity().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songPath);
                songs = new Songs(currentTitle, currentArtist, currentPath);
                songsList.add(songs);
            } while (songCursor.moveToNext());
        }
        songCursor.close();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSON_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }
                } else {
                    Toast.makeText(getContext(), "no permission granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    @Override
    public void onClickListener(String title, String name, final String path) {

        binding.tvSongTitle.setText(title);
        binding.tvSongName.setText(name);
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(path));
        } else {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getContext(), Uri.parse(path));
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mediaPlayer.start();
        binding.btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });
    }
}

