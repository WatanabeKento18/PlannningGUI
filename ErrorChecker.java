import java.util.*;

/**
 *		��Ԃ̕s���𔭌����邽�߂̏���ێ�����N���X
 **/

//��Ԃُ̈�����m����
public class ErrorChecker {
	String name; // �\����
	boolean up, down; // �㉺�ɃI�u�W�F�N�g�͂��邩�ǂ���
	boolean clear, placeable; // ��ɉ����Ȃ�/�z�u�\
	boolean color, shape, triangle; // �F/�`�����肵�Ă��邩,�O�p�`�ł��邩�ǂ���
	boolean error; // 1�ł��G���[������ꍇ
	ArrayList<String> errorList; // �G���[���e

	// �R���X�g���N�^
	public ErrorChecker(String initname) {
		name = initname;
		up = down = clear = placeable = color = shape = triangle = error = false;
		errorList = new ArrayList<String>();
	}

	// ��ɃI�u�W�F�N�g������ꍇ
	public void setAbove() {
		if (!up)
			up = true;
		else {
			error = true;
			errorList.add(name + "�̏�ɔz�u����Ă���I�u�W�F�N�g����������܂��B");
		}
	}

	// ���ɃI�u�W�F�N�g������ꍇ
	public void setUnder() {
		if (!down)
			down = true;
		else {
			error = true;
			errorList.add(name + "����������܂��B");
		}
	}

	// ��ɉ����Ȃ��ꍇ
	public void setClear() {
		if (!clear)
			clear = true;
		else {
			error = true;
			errorList.add("clear " + name + "�������L�q����Ă��܂��B");
		}
	}

	// ��ɃI�u�W�F�N�g��u����ꍇ
	public void setPlaceable() {
		if (!placeable)
			placeable = true;
		else {
			error = true;
			errorList.add("placeable " + name + "�������L�q����Ă��܂��B");
		}
	}

	// �F���̓o�^
	public void setColor() {
		if (!color)
			color = true;
		else {
			error = true;
			errorList.add(name + "�̐F��񂪕����L�q����Ă��܂��B");
		}
	}

	// �`���̓o�^
	public void setShape() {
		if (!shape)
			shape = true;
		else {
			error = true;
			errorList.add(name + "�̌`��񂪕����L�q����Ă��܂��B");
		}
	}
	
	// �O�p�`���̓o�^
	public void setTriangle(){
		triangle = true;
	}

	// �G���[�`�F�b�N�������Ȃ�
	public void scan() {
		if (!down && up) {
			error = true;
			errorList.add(name + "�͔z�u����Ă��܂��񂪁A��ɃI�u�W�F�N�g�����݂��܂��B");
		}
		if (up && clear) {
			error = true;
			errorList.add(name + "�̏�ɃI�u�W�F�N�g�����݂���ɂ��ւ�炸clear " + name
					+ "���L�q����Ă��܂��B");
		}
		if (up && placeable) {
			error = true;
			errorList.add(name + "�̏�ɃI�u�W�F�N�g�����݂���ɂ��ւ�炸placeable " + name
					+ "���L�q����Ă��܂��B");
		}
		if (down && !up && !clear) {
			error = true;
			errorList.add(name + "�̏�ɃI�u�W�F�N�g�����݂��Ȃ��ɂ��ւ�炸clear " + name
					+ "���L�q����Ă��܂���B");
		}
		if (down && !up && !placeable && !triangle) {
			error = true;
			errorList.add(name + "�̏�ɃI�u�W�F�N�g��u����ɂ��ւ�炸placeable " + name
					+ "���L�q����Ă��܂���B");
		}
		if (placeable && triangle) {
			error = true;
			errorList.add(name + "�̏�ɃI�u�W�F�N�g��u���Ȃ��ɂ��ւ�炸placeable " + name
					+ "���L�q����Ă��܂��B");
		}
		if (down && !color) {
			error = true;
			errorList.add(name + "�̐F��񂪋L�q����Ă��܂���B");
		}
		if (down && !shape) {
			error = true;
			errorList.add(name + "�̌`��񂪋L�q����Ă��܂���B");
		}
	}
	
	//�G���[���b�Z�[�W���擾����A�����ꍇ��null��Ԃ�
	public ArrayList<String> getErrorMessage(){
		return errorList;
	}
}