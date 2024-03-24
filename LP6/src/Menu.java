/* Saya Tattha Maharany Yasmin Akbar dengan NIM 2201805 mengerjakan Latihan 6
   dalam mata kuliah Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya
   maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
*/

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(600, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;
    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;
    private JComboBox programStudiComboBox;
    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;
    private  JLabel programStudiLabel;

    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        database = new Database();

        // set model tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));
        String[] programStudiData = {"", "Ilmu Komputer", "Pendidikan Ilmu Komputer"};
        programStudiComboBox.setModel(new DefaultComboBoxModel(programStudiData));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1) {
                    insertData();
                } else {
                    updateData();
                }
            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0) {
                    // Tampilkan dialog konfirmasi
                    int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        deleteData();
                    }
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // saat tombol
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJenisKelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedProgramStudi = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();

                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJenisKelamin);
                programStudiComboBox.setSelectedItem(selectedProgramStudi);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Program Studi"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");

            int i = 0;
            while (resultSet.next()) {
                Object[] row = new Object[5];

                // mengambil data berdasarkan nama kolomnya
                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getString("jenis_kelamin");
                row[4] = resultSet.getString("program_studi");

                temp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return temp;
    }

    // query update data
    public void updateData() {
        if (selectedIndex >= 0) {
            // Peroleh NIM dari baris yang dipilih
            String nimToUpdate = mahasiswaTable.getValueAt(selectedIndex, 1).toString();

            // Ambil data baru dari input pengguna
            String nimBaru = nimField.getText();
            String namaBaru = namaField.getText();
            String jenisKelaminBaru = jenisKelaminComboBox.getSelectedItem().toString();
            String programStudiBaru = programStudiComboBox.getSelectedItem().toString();

            // Buat perintah SQL UPDATE
            String sql = "UPDATE mahasiswa SET nim = '" + nimBaru + "', nama = '" + namaBaru + "', jenis_kelamin = '" + jenisKelaminBaru + "', program_studi = '" + programStudiBaru + "' WHERE nim = '" + nimToUpdate + "'";

            try {
                // Eksekusi perintah SQL UPDATE
                database.insertUpdateDeleteQuery(sql);

                // Update tabel
                mahasiswaTable.setModel(setTable());

                // Bersihkan form
                clearForm();

                // Tampilkan pesan sukses
                System.out.println("Update berhasil!");
                JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
            } catch (Exception e) {
                e.printStackTrace();
                // Tampilkan pesan error
                System.out.println("Gagal mengubah data!");
                JOptionPane.showMessageDialog(null, "Gagal mengubah data!");
            }
        } else {
            // Jika tidak ada baris yang dipilih
            System.out.println("Tidak ada baris yang dipilih!");
            JOptionPane.showMessageDialog(null, "Tidak ada baris yang dipilih!");
        }
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String programStudi = programStudiComboBox.getSelectedItem().toString();

        // Validasi input
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || programStudi.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Semua field harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Hentikan proses insert jika ada input yang kosong
        }
        // Validasi input nim harus unik
        int mark = 0;
        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa"); // cek semua data
            while(resultSet.next()) {
                String existingNim = resultSet.getString("nim"); // cek semua nim
                if(existingNim.equals(nim)) { // jika ada nim pada data ada yang sama dengan nim yang diinput
                    mark = 1; // marking
                }
            }
        } catch (SQLException e) {
        throw new RuntimeException(e);
        }


        if (mark == 1) // jika nim yang baru diinput sudah ada, maka akan menampilkan pesan tidak bisa menambahkan nim
        {
            System.out.println("Nim sudah ada!");
            JOptionPane.showMessageDialog(null, "Gagal menambahkan data! NIM sudah ada");
        }
        else { // jika nim belum ada
            String sql = "INSERT INTO mahasiswa VALUE (null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + programStudi + "');";
            database.insertUpdateDeleteQuery(sql);

            // update tabel
            mahasiswaTable.setModel(setTable());

            // bersihkan form
            clearForm();

            // feedback
            System.out.println("Insert berhasil!");
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan");
        }
    }

    // query delete
    public void deleteData() {
        if (selectedIndex >= 0) {
            // Peroleh NIM dari baris yang dipilih
            String nimToDelete = mahasiswaTable.getValueAt(selectedIndex, 1).toString();

            // Buat perintah SQL DELETE
            String sql = "DELETE FROM mahasiswa WHERE nim = '" + nimToDelete + "'";

            try {
                // Eksekusi perintah SQL DELETE
                database.insertUpdateDeleteQuery(sql);

                // Update tabel
                mahasiswaTable.setModel(setTable());

                // Bersihkan form
                clearForm();

                // Tampilkan pesan sukses
                System.out.println("Delete berhasil!");
                JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
            } catch (Exception e) {
                e.printStackTrace();
                // Tampilkan pesan error
                System.out.println("Gagal menghapus data!");
                JOptionPane.showMessageDialog(null, "Gagal menghapus data!");
            }
        } else {
            // Jika tidak ada baris yang dipilih
            System.out.println("Tidak ada baris yang dipilih!");
            JOptionPane.showMessageDialog(null, "Tidak ada baris yang dipilih!");
        }
    }

    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        programStudiComboBox.setSelectedItem("");

        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }
}
