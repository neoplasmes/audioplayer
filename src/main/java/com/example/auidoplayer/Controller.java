package com.example.auidoplayer;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.json.*;

public class Controller implements Initializable{

    @FXML
    private Pane pane;
    @FXML
    public Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;





    //моё
    @FXML
    private VBox current_playlist;
    @FXML
    private ScrollPane currentPlaylistScroll;
    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private GridPane playlistGrid;

    private ArrayList<String> currentPlaylistMusic;

    private boolean player_isPlaying = false;

    //конец моего

    protected Media media;
    protected MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};

    private Timer timer;
    private TimerTask task;

    private boolean running;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {





        songs = new ArrayList<File>();

        directory = new File("music");

        files = directory.listFiles();

        if(files != null) {

            for(File file : files) {

                songs.add(file);
            }
        }

        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(songs.get(songNumber).getName());

        //for(int i = 0; i < speeds.length; i++) {

        //    speedBox.getItems().add(Integer.toString(speeds[i])+"%");
        //}

        //speedBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {

                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        songProgressBar.setStyle("-fx-accent: #00FF00;");


        //my



        currentPlaylistScroll.setContent(current_playlist);


        playlistGrid.setStyle("-fx-border-color: #000000");

        playlistScroll.setContent(playlistGrid);

        //ColumnConstraints column = new ColumnConstraints();
        //column.setPercentWidth(0.2);


        //RowConstraints row = new RowConstraints();
        //row.setPercentHeight(0.2);


        int test_i = 1;


        String content;
        try {
            content = Files.readString(Paths.get("playlists/playlists.JSON"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonobj = new JSONObject(content);

        JSONArray playlists = jsonobj.getJSONArray("playlists");

        for(int i = 0; i < playlists.length(); i++) {
            JSONObject this_object = playlists.getJSONObject(i);

            Button b = new Button(this_object.getString("name"));

            JSONArray this_playlist_music_json = this_object.getJSONArray("music");

            ArrayList<String> this_playlist_music_paths = new ArrayList<String>();

            for (int j = 0; j < this_playlist_music_json.length(); j++) {
                this_playlist_music_paths.add(this_playlist_music_json.getJSONObject(j).getString("src"));
            }

            System.out.println(this_playlist_music_paths);

            b.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {

                    try {
                        current_playlist.getChildren().clear();
                        updateCurrentPlaylist(this_playlist_music_paths);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

            playlistGrid.add(b, test_i, 1);
            test_i += 1;
        }





    }


    public void updateCurrentPlaylist(ArrayList<String> paths) throws IOException {
        int id = 1;
        String pt_buttonID = "pt_button_";

        System.out.println("1");
        System.out.println(paths);

        for (String path : paths) {

            File m = new File(path);

            playTrackButton pt_button = new playTrackButton(m, pt_buttonID + Integer.toString(id));

            pt_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 1) {
                            songLabel.setText(path);
                        } else if(mouseEvent.getClickCount() == 2) {
                            songLabel.setText(path);
                            media = new Media(m.toURI().toString());
                            mediaPlayer = new MediaPlayer(media);
                            playMedia();
                        }
                    }
                }
            });

            current_playlist.getChildren().add(pt_button);
        }
    }

    public void playMedia() {
        if (!player_isPlaying){
            player_isPlaying = true;
            beginTimer();
            //changeSpeed(null);
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            mediaPlayer.play();

            playButton.setText("pause");
        } else {
            player_isPlaying = false;
            playButton.setText("play");
            pauseMedia();
        }

    }

    public void pauseMedia() {

        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetMedia() {

        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousMedia() {

        if(songNumber > 0) {

            songNumber--;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
        else {

            songNumber = songs.size() - 1;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            System.out.println(songs.get(songNumber).toURI().toString());

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }

    public void nextMedia() {

        if(songNumber < songs.size() - 1) {

            songNumber++;

            mediaPlayer.stop();

            if(running) {

                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
        else {

            songNumber = 0;

            mediaPlayer.stop();

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia();
        }
    }

    public void changeSpeed(ActionEvent event) {

        if(speedBox.getValue() == null) {

            mediaPlayer.setRate(1);
        }
        else {

            //mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {

        timer = new Timer();

        task = new TimerTask() {

            public void run() {

                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current/end);

                if(current/end == 1) {

                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer() {

        running = false;
        timer.cancel();
    }





    //отсюда начинается норм код ffffffffff gggggggg



    @FXML
    private void onDragOverPlaylist(DragEvent event){
        List<File> files = event.getDragboard().getFiles();

        if(files != null && !files.isEmpty()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    private void onDragDroppedPlaylist(DragEvent event) throws IOException {




        List<File> files = event.getDragboard().getFiles();
        List<Button> buttons = new ArrayList<>(); // дост.
        String pt_buttonID = "pt_button_";
        int i = 0;

        for (File file : files) {
            i += 1;

            String songName = file.getName();
            playTrackButton pt_button = new playTrackButton(file, pt_buttonID + Integer.toString(i));

            pt_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 1) {
                            songLabel.setText(songName);
                        } else if(mouseEvent.getClickCount() == 2) {
                            songLabel.setText(songName);
                            media = new Media(file.toURI().toString());
                            mediaPlayer = new MediaPlayer(media);
                            playMedia();
                        }
                    }
                }
            });

            current_playlist.getChildren().add(pt_button);
        }

    }



}
