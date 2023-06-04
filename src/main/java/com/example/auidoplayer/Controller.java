package com.example.auidoplayer;

import javafx.event.EventHandler;
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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
    private AnchorPane pane;
    @FXML
    private TextField changeLabelName;
    @FXML
    public Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton, saveNameButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;





    //Моё
    @FXML
    private VBox current_playlist;
    @FXML
    private ScrollPane currentPlaylistScroll;
    @FXML
    private ScrollPane playlistScroll;
    @FXML
    private FlowPane playlistPane;
    @FXML
    private Label playlistLabel;

    @FXML
    private Button savePlaylistButton;

    private int current_playlist_id;

    public Button plusButton;

    private ArrayList<String> currentPlaylistMusic;

    private JSONObject jsonObject;
    private JSONArray playlists;

    private boolean player_isPlaying = false;

    //Конец моего

    protected static Media media;
    protected static MediaPlayer mediaPlayer;

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



        currentPlaylistMusic = new ArrayList<>();


        //media = new Media(songs.get(songNumber).toURI().toString());
        //mediaPlayer = new MediaPlayer(media);

        //songLabel.setText(songs.get(songNumber).getName());

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


        //My



        currentPlaylistScroll.setContent(current_playlist);


        playlistPane.setStyle("-fx-border-color: #000000");
        playlistPane.setHgap(40.0);
        playlistPane.setVgap(40.0);

        playlistScroll.setContent(playlistPane);

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

        jsonObject = new JSONObject(content);

        playlists = jsonObject.getJSONArray("playlists");


        //Я пока не собираюсь писать то, что здесь происходит
        for(int i = 0; i < playlists.length(); i++) {
            JSONObject this_object = playlists.getJSONObject(i);
            try {
                addNewPlaylist_frontend(this_object, i);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        plusButton = new Button("+");
        plusButton.setOnAction(event -> {
            try {
                addNewPlaylist_frontend();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        playlistPane.getChildren().add(plusButton);
        playlistPane.setMargin(plusButton, new Insets(40.0, 40.0,40.0,40.0));


    }




    public void addNewPlaylist_frontend(JSONObject js_obj, int id) throws IOException {
        /*TextField tf = new TextField();

        pane.getChildren().add(tf);

        tf.setLayoutX(plusButton.getLayoutX() + 5.0);
        tf.setLayoutY(plusButton.getLayoutY() + 5.0);*/



        //playlistPane.getChildren().remove(plusButton);
        //addNewPlaylist_backend();

        //Имя плейлиста который щас прикрепится
        String this_playlist_name = js_obj.getString("name");

        //Ноде к которому будет сейчас прикреплён плейлист
        Button b = new Button(this_playlist_name);

        //Список музыки который сейчас прикрепится
        JSONArray this_playlist_music_json = js_obj.getJSONArray("music");

        //Репрезентация строчки выше в string
        ArrayList<String> this_playlist_music_paths = new ArrayList<String>();

        for (int j = 0; j < this_playlist_music_json.length(); j++) {
            this_playlist_music_paths.add(this_playlist_music_json.get(j).toString());
        }
        //Конец репрезентации

        //System.out.println(this_playlist_music_paths);

        //Добавляем обновление плейлиста на кнопку
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                try {
                    current_playlist.getChildren().clear();
                    updateCurrentPlaylist(this_playlist_music_paths, this_playlist_name, id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        playlistPane.getChildren().add(b);
        playlistPane.setMargin(b, new Insets(40.0, 40.0,40.0,40.0));


        //Button b = new Button();

    }

    //Перегрузка для кнопки ПЛЮС
    public void addNewPlaylist_frontend() throws IOException {

        addNewPlaylist_backend();

        playlistPane.getChildren().remove(plusButton);

        addNewPlaylist_frontend(playlists.getJSONObject(playlists.length() - 1), playlists.length() - 1);

        //Возвращаем кнопку ПЛЮС
        plusButton = new Button("+");
        plusButton.setOnAction(event -> {
            try {
                addNewPlaylist_frontend();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        playlistPane.getChildren().add(plusButton);
        playlistPane.setMargin(plusButton, new Insets(40.0, 40.0,40.0,40.0));
    }


    //Этот метод здесь для настроения
    private static JSONArray listNumberArray(int max){
        JSONArray res = new JSONArray();
        for (int i=0; i<max;i++) {
            //The value of the labels must be an String in order to make it work
            res.put(String.valueOf(i));
        }
        return res;
    }


    //Здесь идет обновление JSON репрезентации плейлиста. Он внесен в отдельный метод, т.к. скорее всего ещё где-то отдельно понадобится
    public void addNewPlaylist_backend() throws IOException {
        /*StringBuilder write = new StringBuilder();
        JSONWriter jsonWriter = new JSONWriter(write);
        */
        JSONObject new_playlist = new JSONObject();
        System.out.println(playlists);
        //имя нового плейлиста
        new_playlist.put("name", "default");

        //Список музыки в новом плейлисте, нужно добавить нормальное добавление
        JSONArray new_music = new JSONArray();
        new_music.put("music/kodacblack.wav");
        new_music.put("music/lilpeep.wav");
        new_music.put("music/waiting.wav");

        //Добавляем список в новый плейлист
        new_playlist.put("music", new_music);

        //Добавляем новый плейлист в общий список плейлистов
        playlists.put(new_playlist);

        //Обновляем файл
        System.out.println(playlists.toString());



        updateJSON(playlists);


        //JSONArray list = listNumberArray(playlists.length());
        //JSONObject object = playlists.toJSONObject(list);
        //System.out.println("Final JSONOBject: " + object);

    }


    //Метод чтобы обновить json файл
    public void updateJSON(JSONArray content) throws IOException {
        JSONObject update = new JSONObject();
        update.put("playlists", content);

        File file = new File("playlists/playlists.JSON");
        FileWriter fw = new FileWriter(file, false);

        fw.write(update.toString());
        fw.close();
    }




    public void clickPlaylistName (MouseEvent event){ // кликнешь 2 раза - поменяешь название плейлиста
        if(event.getButton().equals(MouseButton.PRIMARY)){
            if(event.getClickCount() == 2) {
                changeLabelName.setVisible(true);
                saveNameButton.setVisible(true);
                playlistLabel.setVisible(false);
            }
        }
    }

    public void changePlaylistName(){ // тут крч меняется имя плейлиста
        playlistLabel.setText(changeLabelName.getText());
        changeLabelName.setVisible(false);
        saveNameButton.setVisible(false);
        playlistLabel.setVisible(true);
    }
    //Обновление текущего листа, находящегося сбоку справа
    //Метод принимает список файлов, музыки и имя плейлиста, который сейчас врубится
    public void updateCurrentPlaylist(ArrayList<String> paths, String name, int playlist_id) throws IOException {
        int id = 0;
        String pt_buttonID = "pt_button_";

        playlistLabel.setText(name);

        System.out.println("1");
        System.out.println(paths);

        currentPlaylistMusic = paths;
        current_playlist_id = playlist_id;


        //Добавляем справа а-ля кнопки-треки
        for (String path : paths) {
            //Переменная PATH содержит в себе трек, который сейчас привяжется к кнопке, которая будет его воспроизводить
            File m = new File(path);

            playTrackButton pt_button = new playTrackButton(m, pt_buttonID + Integer.toString(id));

            //Это дубликат кода из строчек гораздо ниже, который я не знаю как не дублировать
            //Здесь просто кнопка запускает трек, который к ней привязан
            pt_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 1) {
                            songLabel.setText(path);
                            songNumber = Integer.parseInt(pt_button.getId().split("_")[2]);
                        } else if(mouseEvent.getClickCount() == 2) {
                            songNumber = Integer.parseInt(pt_button.getId().split("_")[2]);
                            songLabel.setText(path);
                            media = new Media(m.toURI().toString());
                            mediaPlayer = new MediaPlayer(media);
                            playMedia();
                        }
                    }
                }
            });

            //Добавляем кнопку справа
            current_playlist.getChildren().add(pt_button);
            //увеличиваем ид на 1
            id += 1;
        }
    }


    public void savePlaylist() throws IOException {
        //Условие - временное устранение бага
        if (!playlistLabel.getText().equals("Label")){
            //Получаем текущий плейлист
            JSONObject obj = playlists.getJSONObject(current_playlist_id);
            JSONArray arr = new JSONArray(currentPlaylistMusic);
            obj.put("music", arr);

            JSONArray buffer = new JSONArray();

            for (int i = 0; i < playlists.length(); i++) {
                if (i == current_playlist_id){
                    buffer.put(obj);
                } else {buffer.put(playlists.getJSONObject(i));}
            }

            System.out.println(buffer);

            updateJSON(buffer);

            playlists = buffer;
            System.out.println(playlists);

        }
    }


    public void playMedia() {
        if (currentPlaylistMusic.size() > 0) {
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
        if (currentPlaylistMusic.size() > 0) {
            beginTimer();
            playMedia();
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
        if(currentPlaylistMusic.size() > 0) {
            if (songNumber > 0) {

                songNumber--;

                if(player_isPlaying) {
                    mediaPlayer.stop();
                }

                if (running) {

                    cancelTimer();
                }

                File f = new File(currentPlaylistMusic.get(songNumber));

                media = new Media(f.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                songLabel.setText(f.getName());

                if(player_isPlaying) {
                    playMedia(2);
                }
            } else {

                songNumber = currentPlaylistMusic.size() - 1;

                if(player_isPlaying) {
                    mediaPlayer.stop();
                }

                if (running) {

                    cancelTimer();
                }

                File f = new File(currentPlaylistMusic.get(songNumber));

                media = new Media(f.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                songLabel.setText(f.getName());

                if(player_isPlaying) {
                    playMedia(2);
                }
            }
        }
    }

    public void nextMedia() {
        if(currentPlaylistMusic.size() > 0) {
            if (songNumber < currentPlaylistMusic.size() - 1) {

                songNumber++;

                if(player_isPlaying) {
                    mediaPlayer.stop();
                }

                if (running) {

                    cancelTimer();
                }

                File f = new File(currentPlaylistMusic.get(songNumber));

                media = new Media(f.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                songLabel.setText(f.getName());

                if(player_isPlaying) {
                    playMedia(2);
                }
            } else {

                songNumber = 0;

                if(player_isPlaying) {
                    mediaPlayer.stop();
                }

                File f = new File(currentPlaylistMusic.get(songNumber));

                media = new Media(f.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                songLabel.setText(f.getName());

                if(player_isPlaying) {
                    playMedia(2);
                }
            }
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





    //Отсюда начинается хороший код



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
        int id = 0;

        for (File file : files) {


            currentPlaylistMusic.add(file.getAbsolutePath());

            String songName = file.getName();
            playTrackButton pt_button = new playTrackButton(file, pt_buttonID + id);

            pt_button.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                        if(mouseEvent.getClickCount() == 1) {
                            songNumber = Integer.parseInt(pt_button.getId().split("_")[2]);
                            songLabel.setText(songName);

                            media = new Media(file.toURI().toString());
                            mediaPlayer = new MediaPlayer(media);

                        } else if(mouseEvent.getClickCount() == 2) {
                            try {
                                cancelTimer();
                                mediaPlayer.pause();
                            } catch (Exception e){}

                            songNumber = Integer.parseInt(pt_button.getId().split("_")[2]);
                            songLabel.setText(songName);

                            media = new Media(file.toURI().toString());
                            mediaPlayer = new MediaPlayer(media);

                            playMedia();
                        }
                    }
                }
            });

            current_playlist.getChildren().add(pt_button);
            id += 1;
        }

    }
    public void deleteSong (ActionEvent event){
        for (int i = 0; i<files.length; i++){
            System.out.println(files[i].getName());
            System.out.println(songLabel.getText());
            if (files[i].getName().equals((songLabel.getText().split("/"))[1])) {
                current_playlist.getChildren().remove(i);
                break;
            }
        }
    }


}
