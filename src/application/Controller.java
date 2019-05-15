package application;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;
import javafx.util.StringConverter;


public class Controller implements Initializable{
	public String[] daysOfWeek = {"��", "һ", "��", "��", "��", "��", "��"};
	public String[] classNames;
	public ArrayList<Student> students;  // ѧ������
	public int room = 0;  // ��ǰ������
	public int[] seats;  // һ�������ҵ���λ״̬
	public SeatView[][] seatViews;  // ��λ��
	public String lastId;
	public String lastRoom;
	public String lastSeat;
	
	// Left
	@FXML
	private DatePicker datePicker;
	
	@FXML
	private TextField hourTextField;
	
	@FXML
	private TextField minuteTextField;
	
	@FXML
	private Button queryButton;  // ��ѯ
	
	@FXML
	private TextField weekTextField;  // ��ѧ��
	
	@FXML
	private TextField dayTextField;  // ����
	
	@FXML
	private Label tip;  // ˵����ѧ��ʼ��
	
	@FXML
	private TabPane tabPane;
	
	@FXML
	private Tab tab0;
	
	@FXML
	private FlowPane seatPane;  // ������
	
	@FXML
	private Button legend1;  // ռ��
	
	@FXML
	private Button legend2;  // ����
	
	@FXML
	private Button legend3;  // ����
	
	@FXML
	private TextField seatInfo;
	
	// Right
	@FXML
	private TableView<Student> studentTable;
	
	@FXML
	private TextField newId;
	
	@FXML
	private TextField chooseClass;
	
	@FXML
	private TextField chooseRoom;
	
	@FXML
	private TextField chooseSeat;
	
	@FXML
	private Button addButton;
	
	@FXML
	private ImageView imageView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Image image = new Image(Util.IMAGE_OF_EXCEL_PATH);
		imageView.setImage(image);

		// Ϊͼ����ע��ɫ
		legend1.setStyle(Util.BUSY_STYLE);
		legend2.setStyle(Util.FREE_STYLE);
		legend3.setStyle(Util.NONE_STYLE);
		
		// ���ó�ʼʱ��Ϊ��ǰʱ��
		LocalDateTime now = LocalDateTime.now();
		datePicker.setValue(now.toLocalDate());
		datePicker.setEditable(false);
		hourTextField.setText(String.valueOf(now.getHour()));
		minuteTextField.setText(String.valueOf(now.getMinute()));
		weekTextField.setText(String.valueOf(getWeek(now.toLocalDate())+1));
		dayTextField.setText(daysOfWeek[now.toLocalDate().getDayOfWeek().getValue()%7]);
		
		tip.setText("����1�ܵ���һΪ"+Util.WEEK_INIT+"��");
		weekTextField.setEditable(false);

		
		// ��ʼ����λ��
		seatPane.setPadding(new Insets(8, 8, 8, 8));
		seatPane.setHgap(Util.Hgap);
		seatPane.setVgap(Util.Vgap);
		seatInfo.setEditable(false);
		SeatView.seatInfo = seatInfo;  // ��λ��Ϣչʾ
		seatViews = null;
		// TODO seats = query(room, LocalDateTime);
//		for(int i=0;i<Util.ROOM_NUM;i++){
			int[] a = {0, 1, -1, 50, 55, 20, -20, -35, -90, 10};   //ͨ��n����״ֵ̬������λ��n<=81�� 
			seats = a;  
			initSeat(room, seats);
//		}
		
		tab0.setOnSelectionChanged(new EventHandler<Event>() {  // ��0��������
			@Override
			public void handle(Event event) {
				FlowPane blank = new FlowPane();
				Tab tab = (Tab) event.getSource();
				if(tab.isSelected()){
					room = 0;
					tab.setContent(blank);
				//  TODO seats = query(room, LocalDateTime);
					int[] a = {0, 1, -1, 50, 55, 2, -20, -35, -90, 10,0, 1, -1, 50, 55};  
					seats = a;
					updateSeat(room, seats);
					tab.setContent(seatPane);
				}
			}
		});
		
		for(int i=1;i<Util.ROOM_NUM;i++){  // ���������
			Tab tab = new Tab();
			tab.setText("������"+i);
			tab.setId(String.valueOf(i));
			tabPane.getTabs().add(tab);
			tab.setOnSelectionChanged(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					FlowPane blank = new FlowPane();
					Tab tab = (Tab) event.getSource();
					if(tab.isSelected()){
						room = Integer.parseInt(tab.getId());
						tab.setContent(blank);
						//  TODO seats = query(room, LocalDateTime);
						int[] a = {0, 1, -1, 50, 55, 20, -20, -35, -90, 10,0, 1, -1, 50, 55, 20, -20, -35, -90, 10, -20, -35, -90, 10,0, 1, -1, 50, 55};
						seats = a;
						updateSeat(room, seats);
						tab.setContent(seatPane);
						}
					}
				});
			}
		
		
		//  TODO ��ʼѧ����Ϣ�Ͱ༶����
		String [] c = {"������151105", "������151106","������151107","������151108"};
		classNames = c;
		students = new ArrayList<Student>();
		students.add(new Student(201853201, "������151105", 0, 3));
		students.add(new Student(201853202, "������151106", 1, 7));
		students.add(new Student(201853203, "������151107", 2, 5));
		students.add(new Student(201853204, "������151106", 6, 2));
		students.add(new Student(201853205, "������151105", 3, 4));
		students.add(new Student(201853206, "������151108", 5, 55));
//		students.add(new Student(201853207, "������151105", 3, 2));
//		students.add(new Student(201853208, "������151107", 6, 36));
		studentTable.setEditable(true);
		ObservableList<Student> studentData = FXCollections.observableArrayList(students);

		// ѧ���� 
		TableColumn<Student, Number>  idCol = new TableColumn<Student, Number>("ѧ��");
		idCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,Number>, ObservableValue<Number>>() {
			@Override
			public ObservableValue<Number> call(CellDataFeatures<Student, Number> param) {
				return param.getValue().getIdProperty();
			}
		});
		idCol.setMinWidth(100);
		idCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>(){
			@Override
			public String toString(Number object) {
				lastId = String.valueOf(object.intValue());
				return String.valueOf(object.intValue());
			}
			@Override
			public Number fromString(String string) {
				try{
				return Integer.valueOf(string);
				}catch(NumberFormatException e){
					showError(Util.NUMBER_ERROR);
					return Integer.valueOf(lastId);
				}
			}
			
		}));
		idCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, Number>>(){
			@Override
			public void handle(CellEditEvent<Student, Number> event) {
				int row = event.getTablePosition().getRow();
				boolean flag = true;
				for(int i=0;i<students.size();i++){
					if(i!=row&&students.get(i).getId()==event.getNewValue().intValue()){
						flag = false;
						break;
					}
				}
				if(flag){
					students.get(row).setId(event.getNewValue().intValue());
				}else{
					students.get(row).setId(event.getOldValue().intValue());
					showError(Util.ID_ERROR);
					studentTable.refresh();
				}
			}
		});
		// �༶��
		TableColumn<Student, String>  classCol = new TableColumn<Student, String>("�༶");
		classCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Student, String> param) {
				return param.getValue().getBelongClassProperty();
			}
		});
		classCol.setMinWidth(150);
		classCol.setCellFactory(TextFieldTableCell.forTableColumn());
		classCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, String>>(){
			@Override
			public void handle(CellEditEvent<Student, String> event) {
				int row = event.getTablePosition().getRow();
				boolean flag = false;
				for(int i=0;i<classNames.length;i++){
					if(event.getNewValue().equals(classNames[i])){
						flag = true;
					}
				}
				if(flag){
					students.get(row).setBelongClass(event.getNewValue());
				}else{
					students.get(row).setBelongClass(event.getOldValue());
					showError(Util.CLASSNAME_ERROR);
					studentTable.refresh();
				}
			}
		});
//		// �������� 
		TableColumn<Student, Number>  roomCol = new TableColumn<Student, Number>("������");
		roomCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,Number>, ObservableValue<Number>>() {
			@Override
			public ObservableValue<Number> call(CellDataFeatures<Student, Number> param) {
				return param.getValue().getRoomProperty();
			}
		});
		roomCol.setMinWidth(75);
		roomCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>(){
			@Override
			public String toString(Number object) {
				lastRoom = String.valueOf(object.intValue());
				return String.valueOf(object.intValue());
			}
			@Override
			public Number fromString(String string) {
				try{
				return Integer.valueOf(string);
				}catch(NumberFormatException e){
					showError(Util.NUMBER_ERROR);
					return Integer.valueOf(lastRoom);
				}
			}
			
		}));
		roomCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, Number>>(){
			@Override
			public void handle(CellEditEvent<Student, Number> event) {
				int row = event.getTablePosition().getRow();
				if(isEmptySeat(event.getNewValue().intValue(),students.get(row).getSeat())){
					students.get(row).setRoom(event.getNewValue().intValue());
				}else{
					students.get(row).setRoom(event.getOldValue().intValue());
					studentTable.refresh();
				}
				
			}
		});
		// ��λ����
		TableColumn<Student, Number>  seatCol = new TableColumn<Student, Number>("��λ��");
		seatCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Student,Number>, ObservableValue<Number>>() {

			@Override
			public ObservableValue<Number> call(CellDataFeatures<Student, Number> param) {
				return param.getValue().getSeatProperty();
			}
		});
		seatCol.setMinWidth(75);
		seatCol.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Number>(){
			@Override
			public String toString(Number object) {
				lastSeat = String.valueOf(object.intValue());
				return String.valueOf(object.intValue());
			}
			@Override
			public Number fromString(String string) {
				try{
				return Integer.valueOf(string);
				}catch(NumberFormatException e){
					showError(Util.NUMBER_ERROR);
					return Integer.valueOf(lastSeat);
				}
			}
			
		}));
		seatCol.setOnEditCommit(new EventHandler<CellEditEvent<Student, Number>>(){
			@Override
			public void handle(CellEditEvent<Student, Number> event) {
				int row = event.getTablePosition().getRow();
				if(isEmptySeat(students.get(row).getRoom(), event.getNewValue().intValue())){
					students.get(row).setSeat(event.getNewValue().intValue());
				}else{
					students.get(row).setSeat(event.getOldValue().intValue());
					studentTable.refresh();
				}
			}
		});
		// ɾ��
		TableColumn<Student, Integer> deleteCol = new TableColumn<Student, Integer>("ɾ��");
        deleteCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        deleteCol.setCellFactory(new Callback<TableColumn<Student, Integer>, TableCell<Student, Integer>>() {
            @Override
            public TableCell<Student, Integer> call(final TableColumn<Student, Integer> param) {
                final TableCell<Student, Integer> cell = new TableCell<Student, Integer>() {
                    final Button btn = new Button("ɾ��");
                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	Alert deleteStudentAlert = new Alert(AlertType.CONFIRMATION);
                            	deleteStudentAlert.setTitle("confirmation");
                            	deleteStudentAlert.setHeaderText(Util.DELETE_STUDENT_CONFIRM);
            					Optional<ButtonType> result = deleteStudentAlert.showAndWait();
            					if (result.get() == ButtonType.OK){
	                                getTableView().getItems().remove(getIndex());
	                                students.remove(getIndex());
            					}
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        // �޸�
        TableColumn<Student, Integer> alterCol = new TableColumn<Student, Integer>("�޸�");
        alterCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        alterCol.setCellFactory(new Callback<TableColumn<Student, Integer>, TableCell<Student, Integer>>() {
            @Override
            public TableCell<Student, Integer> call(final TableColumn<Student, Integer> param) {
                final TableCell<Student, Integer> cell = new TableCell<Student, Integer>() {

                    final Button btn = new Button("�޸�");

                    @Override
                    public void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction(event -> {
                            	// TODO 
                                Alert tipAlert = new Alert(AlertType.INFORMATION);
                        		tipAlert.setTitle("tip");
                        		tipAlert.setHeaderText(Util.TIP_INFO);
                        		tipAlert.show();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        
        //  �ϲ�����
        TableColumn<Student, Object> actionCol = new TableColumn<Student, Object>("����");
        actionCol.getColumns().add(alterCol);
        actionCol.getColumns().add(deleteCol);
              
		studentTable.setItems(studentData);  // ��������
		studentTable.getColumns().add(idCol);
		studentTable.getColumns().add(classCol);
		studentTable.getColumns().add(roomCol);
		studentTable.getColumns().add(seatCol);
		studentTable.getColumns().add(actionCol);
		
		addButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try{
					int id = Integer.parseInt(newId.getText());
					String className = chooseClass.getText();
					int room = Integer.parseInt(chooseRoom.getText());
					int seat = Integer.parseInt(chooseSeat.getText());
					addStudent(id, className, room, seat);
				}catch(NumberFormatException e){
					showError(Util.NUMBER_ERROR);
				}
			}
		});
		//  TODO ��ʼ���γ̱�
		
		
	}
	
	// ����ʱ���ѯ��λʹ�����
	@FXML
	public void query(){
		LocalDateTime dateTime = getDateTime();
		//�Ƿ�ɹ���ȡʱ��
		if(dateTime!=null){
			weekTextField.setText(String.valueOf(getWeek(dateTime.toLocalDate())+1));
			dayTextField.setText(daysOfWeek[dateTime.toLocalDate().getDayOfWeek().getValue()%7]);
			//  TODO seats = query(room, LocalDateTime);
			int[] a = {0, 1, -1, 50, 55, 20, -20, -35, -90, 10,0, 1, -1, 50, 55, 20, -20, -35, -90, 10, -20, -35, -90, 10,0, 1, -1, 50, 55};
			seats = a;
			updateSeat(room, seats);
		}else{
			showError(Util.DATETIME_ERROR);
		}
	}
	
	// ��ȡ����ʱ��
	public LocalDateTime getDateTime(){
		try{
			LocalDate localDate = datePicker.getValue();
			int hour = Integer.parseInt(hourTextField.getText());
			int minute = Integer.parseInt(minuteTextField.getText());
			LocalTime localTime = LocalTime.of(hour, minute);
			LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
			return localDateTime;
		}catch(NumberFormatException|NullPointerException|DateTimeException e){
			return null;
		}
	}
	
	// ��ʼ����λ
	public void initSeat(int room, int[] seats){
		seatViews = new SeatView[Util.ROOM_NUM][Util.ROOM_SPACE];  // ��������Ŀ������������, �����СΪ8��81
		int id;
		for(id=0;id<seatViews[room].length;id++){  
			if(id<seats.length){
				seatViews[room][id] = new SeatView(room, id, seats[id]);
			}else{
				seatViews[room][id] = new SeatView(room, id);  // Ĭ��״̬Ϊδ����
			}
		}
		// add Node
		for(int j=0;j<seatViews[room].length;j++){
			seatPane.getChildren().add(seatViews[room][j].getSeatButton());
		}
	}

	// ��ȡָ������������ѧ��,����0�����һ�ܣ��Դ�����
	public long getWeek(LocalDate date){
		long week;
		// ��һ�ܵ���һΪ2019-3-4
		LocalDate weekOne = LocalDate.of(Integer.parseInt(Util.WEEK_INIT.split("-")[0])
				, Integer.parseInt(Util.WEEK_INIT.split("-")[1])
				, Integer.parseInt(Util.WEEK_INIT.split("-")[2]));
		week = (date.toEpochDay()-weekOne.toEpochDay())/7;
		return week;
	}
	
	//  ������λ״̬
	public void updateSeat(int room, int[] seats){
		seatPane.getChildren().remove(0, Util.ROOM_SPACE);
		initSeat(room, seats);
	}
	
	//  TODO ����Library�е�seats��ע�⣺Library�е�query�����ȼ��seatViews����seatViews��Ϊ�գ�����seatViews����seats
	public int [][] getSeats(){
		int [][] seats = new int[Util.ROOM_NUM][Util.ROOM_SPACE];
		if(seatViews!=null){
			for(int i=0;i<Util.ROOM_NUM;i++){
				for(int j=0;j<Util.ROOM_SPACE;j++){
					seats[i][j]=seatViews[i][j].getState();
				}
			}
			return seats;
		}else{
			return null;
		}
	}
	
	//  ���ѧ��
	public boolean addStudent(int id, String className, int room, int seat){
		if(isStudent(id, className, room, seat)){
			students.add(new Student(id, className, room, seat));
			studentTable.getItems().add(new Student(id, className, room, seat));
			if(this.room == room){
				// TODO  seats = query(room, LocalDateTime);updateSeat(m, seats);
			}
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isStudent(int id, String className, int room, int seat){
		for(int i=0;i<students.size();i++){  // �ж�ѧ���Ƿ��Ѵ���
			if(students.get(i).getId()==id){
				showError(Util.ID_ERROR);
				return false;
			}
		}
		boolean flag = false;
		for(int i=0;i<classNames.length;i++){  // �Ұ༶
			if(classNames[i].equals(className)){
				flag = true;
				break;
			}
		}
		if(!flag){
			showError(Util.CLASSNAME_ERROR);
			return flag;
		}
		if(room<0||room>=Util.ROOM_NUM){
			showError(Util.ROOM_ERROR);
			return false;
		}
		if(seat<0||seat>=Util.ROOM_SPACE){
			showError(Util.SEAT_ERROR);
			return false;
		}
		if(!isEmptySeat(room, seat)){
			return false;
		}
		return true;
	}
	
	public boolean isEmptySeat(int room, int seat){
		System.out.println();
		//TODO seats = query(room, LocalDateTime);if(seats[seat]==0){
		if(seatViews[room][seat].getState()==0){  // δ������λ
			showError(Util.SEAT_ERROR);
			return false;
		}
		for(int i=0;i<students.size();i++){  // �ж��Ƿ���Ϊvip��λ
			if(students.get(i).getRoom()==room&&students.get(i).getId()==seat){
				showError(Util.OCCUPY_ERROR);
				return false;
			}
		}
		return true;
	}
	
	public void showError(String error){
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setTitle("error");
		errorAlert.setHeaderText(error);
		errorAlert.show();
	}
}
