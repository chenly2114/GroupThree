package application;

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SeatView {
	public static TextField seatInfo;  // ��̬չʾ��λ��Ϣ
	private int room;  // �����
	private int id;  // ��λ��
	private int state; // ȡֵ-a��0��+b���ֱ��ʾ��������ռ��a���ӡ�δ����λ��������b���ӣ�a��b>0��
	private Button seatButton;  
	
	// ���캯��
	public SeatView(int room, int id, int state){
		this.setRoom(room);
		this.setId(id);
		this.setSeatButton(new Button());
		seatButton.setPrefSize(Util.prefWidth, Util.prefHeight);
		this.setState(state);
	}
	
	public SeatView(int room, int id){  // Ĭ������
		this.setRoom(room);
		this.setId(id);
		this.setSeatButton(new Button());
		seatButton.setPrefSize(Util.prefWidth, Util.prefHeight);
		this.setState(0);
	}
	
	public String getInfo(){  // ��ȡչʾ��Ϣ
		String info;
		if(state < 0){
			info = room+"��"+id+"��"+"��δ��"+(-state)+"�����ڱ�ռ��";
		}else if(state == 0){
			info = "������������λ";
		}else{
			info = room+"��"+id+"��"+"��δ��"+(state)+"�����ڿ���";
		}
		return info;
	}

	public int getRoom() {
		return room;
	}

	public void setRoom(int room) {
		this.room = room;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getState(){
		return state;
	}
	
	public void setState(int state){
		this.state = state;
		//��ʼ��button��ʽ
		String buttonColor;
		if(this.state < 0){
			buttonColor = Util.BUSY_STYLE;
			seatButton.setOnAction(null);
		}else if(this.state == 0){
			buttonColor = Util.NONE_STYLE;
			seatButton.setOnAction(new EventHandler<ActionEvent>(){  // ���������λ
				@Override
				public void handle(ActionEvent event) {
					Alert addSeatAlert = new Alert(AlertType.CONFIRMATION);
					addSeatAlert.setTitle("confirmation");
					addSeatAlert.setHeaderText(Util.ADD_SEAT_CONFIRM);
					Optional<ButtonType> result = addSeatAlert.showAndWait();
					if (result.get() == ButtonType.OK){
					    setState(1400);  // δ��24Сʱ�ڿ���
					    seatInfo.setText(getInfo());
					}
				}
			});
		}else{
			buttonColor = Util.FREE_STYLE;
			seatButton.setOnAction(null);
		}
		seatButton.setStyle(buttonColor);

		seatButton.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				seatInfo.setText(getInfo());
			}
		});
		seatButton.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				seatInfo.setText("");
			}
		});
	}
	
	public Button getSeatButton(){
		return seatButton;
	}
	
	public void setSeatButton(Button seatButton){
		this.seatButton = seatButton;
	}
}
