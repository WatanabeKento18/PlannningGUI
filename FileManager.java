import java.io.*;
import java.util.*;

/**
 * 		�t�@�C���̓��o�͑���
 **/

class FileManager {

	// �w��̃t�@�C���ɏ�Ԃ������o��
	public static void saveState(File file, Vector<String> state) {
		try {
			FileWriter fw = new FileWriter(file); // �������ޏ���
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0 ; i < state.size() ; i++ ) { // ��Ԃ��ׂĂɑ΂��ď��Ԃɏ����������Ȃ�
				bw.write(state.elementAt(i)); // ��ԏo��
				if( i < state.size()-1 ) bw.newLine();	//���s
			}
			bw.close(); // �g�����炵�܂�
		} catch (IOException e) { // ��O����
			System.out.println(e); // �G���[���b�Z�[�W�\��
		}
	}

	// �w��̃t�@�C�������Ԃ�ǂݍ���
	public static Vector<String> loadState(File file) {
		Vector<String> state = new Vector<String>();
		try { // �t�@�C���ǂݍ��݂Ɏ��s�������̗�O�����̂��߂�try-catch�\��
			// �����R�[�h���w�肵��BufferedReader�I�u�W�F�N�g�����
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file.getCanonicalPath()), "UTF-8"));

			// �ϐ�line��1�s���ǂݍ���for��
			for (String line = in.readLine(); line != null; line = in.readLine()) {
				state.addElement(line);	//1�s���ǉ�
			}
		} catch (IOException e) {
			e.printStackTrace(); // ��O�������������܂ł̃X�^�b�N�g���[�X��\��
		}
		return state;
	}
}