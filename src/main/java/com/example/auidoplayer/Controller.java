package com.example.auidoplayer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import java.io.File;
import java.io.FileWriter;
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
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.json.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane pane;
    @FXML
    private TextField changeLabelName;
    @FXML
    public Label songLabel;
    @FXML
    private Button playButton, nextButton, saveNameButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    private ProgressBar changeProgressBar;
    @FXML
    private VBox current_playlist;
    public double playlistWidth = 0.0;
    @FXML
    private ScrollPane currentPlaylistScroll;
    @FXML
    private Label playlistLabel;
    @FXML
    private Label currentTime;
    @FXML
    private Label fullTime;
    @FXML
    Label tip;
    private ArrayList<trackButton> songs;
    private boolean player_isPlaying = false;
    protected static Media media;
    protected static MediaPlayer mediaPlayer;

    private int songNumber;
    private Timer timer;

    private boolean running;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        tip.setVisible(false);
        currentTime.setVisible(false);
        fullTime.setVisible(false);

        songs = new ArrayList<>();

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                if (!(mediaPlayer == null)) {
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                }
            }
        });

        songProgressBar.setStyle("-fx-accent: #abebdd;");

        changeProgressBar = new ProgressBar(0);
        changeProgressBar.setStyle("-fx-accent: #abebdd; -fx-opacity: 0.5;");
        changeProgressBar.setPrefWidth(songProgressBar.getPrefWidth());
        changeProgressBar.setPrefHeight(songProgressBar.getPrefHeight());
        changeProgressBar.setBlendMode(BlendMode.MULTIPLY);

        pane.getChildren().add(changeProgressBar);
        AnchorPane.setTopAnchor(changeProgressBar, songProgressBar.getLayoutY());
        AnchorPane.setLeftAnchor(changeProgressBar, songProgressBar.getLayoutX());
        changeProgressBar.addEventHandler(MouseEvent.ANY, mouseEvent -> progressBarHandler(mouseEvent));

        System.out.println(currentPlaylistScroll.getPrefWidth());

        currentPlaylistScroll.getStylesheets().add(getClass().getResource("scrollpane.css").toString());
        currentPlaylistScroll.setContent(current_playlist);

        playlistWidth = currentPlaylistScroll.getPrefWidth() - 15;

        currentPlaylistScroll.setMaxWidth(currentPlaylistScroll.getPrefWidth());
        currentPlaylistScroll.setMinWidth(currentPlaylistScroll.getPrefWidth());

        current_playlist.setPrefWidth(playlistWidth);

        String data;
        try {
            data = Files.readString(Paths.get("playlists/playlists.JSON"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = new JSONObject(data);
        JSONArray arr = jo.getJSONArray("tracks");

        List<File> list = new ArrayList<>();
        for (Object o : arr){
            list.add(new File(o.toString()));
        }

        if(list.size() > 0) {uploadTracks(list);}
    }


    //Метод чтобы обновить json файл
    public void savePlaylist() throws IOException {
        JSONArray array = new JSONArray();

        for (trackButton b : songs){
            array.put(b.path);
        }

        JSONObject update = new JSONObject();
        update.put("tracks", array);

        File file = new File("playlists/playlists.JSON");
        FileWriter fw = new FileWriter(file, false);

        fw.write(update.toString());
        fw.close();
    }


    public void clickPlaylistName(MouseEvent event) { // кликнешь 2 раза - поменяешь название плейлиста
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                changeLabelName.setVisible(true);
                saveNameButton.setVisible(true);
                playlistLabel.setVisible(false);
            }
        }
    }

    public void changePlaylistName() { // тут крч меняется имя плейлиста
        playlistLabel.setText(changeLabelName.getText());
        changeLabelName.setVisible(false);
        saveNameButton.setVisible(false);
        playlistLabel.setVisible(true);
    }


    public void playMedia() {
        if (!(mediaPlayer == null)) {
            if (!player_isPlaying) {
                player_isPlaying = true;
                beginTimer();
                //changeSpeed(null);
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                mediaPlayer.play();

                playButton.setText("⏸");
            } else {
                player_isPlaying = false;
                playButton.setText("▶");
                pauseMedia();
            }
        }
    }

    //перегрузка для кнопок переключения трека
    public void playMedia(int mode) {
        if (songs.size() > 0 && !(mediaPlayer == null)) {
            //mediaPlayer.getAudioEqualizer().getBands().get(3).setGain(EqualizerBand.MAX_GAIN / 3 * 2);
            beginTimer();
            mediaPlayer.play();
        }
    }

    public void pauseMedia() {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void previousMedia() {
        if (!(mediaPlayer == null)) {

            if ((mediaPlayer.getCurrentTime().toSeconds() <= 5.0) && (songs.size() > 0)) {
                songProgressBar.setProgress(0);
                if (songNumber > 0) {
                    songNumber--;

                    if (player_isPlaying) {
                        mediaPlayer.stop();
                    }
                    if(running){
                        cancelTimer();
                    }

                    File f = new File(songs.get(songNumber).path);

                    setMedia(f);

                    if (player_isPlaying) {
                        playMedia(2);
                    }
                } else {
                    songNumber = songs.size() - 1;

                    if (player_isPlaying) {
                        mediaPlayer.stop();
                    }
                    if(running){
                        cancelTimer();
                    }
                    File f = new File(songs.get(songNumber).path);
                    setMedia(f);
                    if (player_isPlaying) {
                        playMedia(2);
                    }
                }
            } else {
                songProgressBar.setProgress(0);
                mediaPlayer.seek(Duration.seconds(0));
            }
        }
    }

    public void nextMedia() {
        if (songs.size() > 1 && !(mediaPlayer == null)) {
            songProgressBar.setProgress(0);
            if (songNumber < songs.size() - 1) {
                songNumber++;

                if (player_isPlaying) {
                    mediaPlayer.stop();
                }
                if(running){
                    cancelTimer();
                }
                File f = new File(songs.get(songNumber).path);

                setMedia(f);
                if (player_isPlaying) {
                    playMedia(2);
                }
            } else {
                songNumber = 0;
                if (player_isPlaying) {
                    mediaPlayer.stop();
                }
                if(running){
                cancelTimer();
                }
                File f = new File(songs.get(songNumber).path);
                setMedia(f);
                if (player_isPlaying) {
                    playMedia(2);
                }
            }
        }
    }

    public void beginTimer() {
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toMinutes();
                double end = media.getDuration().toMinutes();
                songProgressBar.setProgress(current / end);

                String minutes = String.format("%02d", (int) Math.floor(current));
                String seconds = String.format("%02d", Math.round((current % 1.0) * 60.0));
                Platform.runLater(() -> {currentTime.setText(minutes + ":" + seconds);});
                if (current / end == 1) {cancelTimer();}
            }
        }, 0, 100);
    }

    public void cancelTimer() {
        running = false;
        timer.cancel();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //Обработка прогресс бара

    public void progressBarHandler(MouseEvent event) {
        Bounds b = songProgressBar.getBoundsInParent();
        double left = b.getMinX();
        double right = b.getMaxX();
        double mouseX = event.getSceneX();
        double p = (mouseX-left+2) / (right - left-2);

        if (!(mediaPlayer == null)) {
            double t = mediaPlayer.getTotalDuration().toMinutes() * p;
            if (event.getEventType().equals(MouseEvent.MOUSE_EXITED)) {
                tip.setVisible(false);
                changeProgressBar.setProgress(0);
            }
            if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)) {
                tip.setVisible(true);
            }
            if (event.getEventType().equals(MouseEvent.MOUSE_ENTERED)
                    || event.getEventType().equals(MouseEvent.MOUSE_MOVED)
                    || event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)
                    || event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                if ((mouseX >= left) && (mouseX <= right)) {
                    tip.setVisible(true);

                    AnchorPane.setLeftAnchor(tip, mouseX - 10);

                    AnchorPane.setTopAnchor(tip, b.getMinY() - 20);

                    String minutes = String.format("%02d", (int) Math.floor(t));
                    String seconds = String.format("%02d", Math.round((t % 1.0) * 60.0));

                    tip.setText(minutes + ":" + seconds);

                    changeProgressBar.setProgress(p);
                }
            }

            if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
                songProgressBar.setProgress(p);
                mediaPlayer.seek(Duration.minutes(t));
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------
    //Обработка плейлиста


    @FXML
    private void onDragOverPlaylist(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();

        if (files != null && !files.isEmpty()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    @FXML
    private void onDragDroppedPlaylist(DragEvent event) throws IOException {
        List<File> files = event.getDragboard().getFiles();

        uploadTracks(files);
    }

    public void clearPlaylist() {
        songNumber = 0;
        songs.clear();
        current_playlist.getChildren().clear();
        nextButton.setStyle("-fx-background-color:  #bababa");
    }

    public void onAddTrackButton() {
        try {
            FileChooser fileChooser = new FileChooser();
            List<File> files = fileChooser.showOpenMultipleDialog(pane.getScene().getWindow());

            uploadTracks(files);
        } catch (Exception e) {
        }
    }


    public void onUploadButton() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File dir = directoryChooser.showDialog(pane.getScene().getWindow());

            playlistLabel.setText(dir.getName());

            List<File> files = new ArrayList<>(Arrays.asList(dir.listFiles()));
            songNumber = 0;
            songs.clear();
            current_playlist.getChildren().clear();
            uploadTracks(files);
        } catch (Exception e) {
        }
    }

    public void uploadTracks(List<File> files) {

        int k = 0;

        while (k < files.size()) {
            File file = files.get(k);

            //скип папки и добавление в треков из неё в массив
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if(file.getName().endsWith(".mp3")
                            || file.getName().endsWith(".wav")){
                        files.add(f);
                    }
                }
                k += 1;
                continue;
            }

            if(file.getName().endsWith(".mp3")
                    || file.getName().endsWith(".wav")){
                createTrackButton(file);
            }

            if (songs.size() > 1) {
                nextButton.setStyle("-fx-background-color:  #ffbc00");
            }

            k += 1;
        }
    }

    public void createTrackButton(File file) {
        trackButton trackbutton = new trackButton(file, playlistWidth);

        trackbutton.label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 1) {
                        try {
                            cancelTimer();

                            songProgressBar.setProgress(0.0);

                            player_isPlaying = false;
                            playButton.setText("▶");

                            mediaPlayer.pause();
                        } catch (Exception e) {
                        }
                        songNumber = songs.indexOf(trackbutton);

                        setMedia(file);

                    } else if (mouseEvent.getClickCount() == 2) {
                        try {
                            cancelTimer();
                            mediaPlayer.pause();
                        } catch (Exception e) {
                        }

                        songNumber = songs.indexOf(trackbutton);

                        setMedia(file);
                        playMedia();
                    }
                }
            }
        });

        trackbutton.deleteButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                    int index = songs.indexOf(trackbutton);

                    songNumber--;

                    current_playlist.getChildren().remove(index);
                    songs.remove(index);

                    if (songs.size() <= 1) {
                        nextButton.setStyle("-fx-background-color:  #bababa");
                    }
                }
            }
        });

        songs.add(trackbutton);
        current_playlist.getChildren().add(trackbutton);
    }

    public void setMedia(File file) {
        media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(file.getName());
        mediaPlayer.setOnReady(new Runnable() {
            @Override
            public void run() {
                currentTime.setVisible(true);
                currentTime.setText("00:00");

                fullTime.setVisible(true);
                double t = mediaPlayer.getTotalDuration().toMinutes();
                String minutes = String.format("%02d", (int) Math.floor(t));
                String seconds = String.format("%02d", Math.round((t % 1.0) * 60.0));
                fullTime.setText(minutes + ":" + seconds);
            }
        });
    }

}





