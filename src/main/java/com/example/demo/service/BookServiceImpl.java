package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.Dealer;
import com.example.demo.model.PendingBook;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.PendingBookRepository;
import com.example.demo.repository.DealerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> getByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    @Override
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    public List<Book> getBooksByDealer(Long dealerId) {
        return bookRepository.findByDealer_Id(dealerId);
    }

    // ✅ Approved books
    @Override
    public List<Book> getApprovedBooks() {
        return bookRepository.findByStatus("Available");
    }

    // ✅ Pending books (admin reviews)
    @Override
    public List<Book> getPendingBooks() {
        return bookRepository.findByStatus("PENDING");
    }

    // ✅ Approve book
    @Override
    public void approveBook(Long id) {
        Book book = getBookById(id);
        if (book != null) {
            book.setStatus("Available");
            bookRepository.save(book);
        }
    }

    // ✅ Reject book
    @Override
    public void rejectBook(Long id) {
        Book book = getBookById(id);
        if (book != null) {
            book.setStatus("REJECTED");
            bookRepository.save(book);
        }
    }

    @Autowired
    private PendingBookRepository pendingBookRepository;  // ✅ MISSING FIXED

    @Override
    public void processUploadedFile(MultipartFile file, Long dealerId) throws Exception {

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new Exception("Invalid file");
        }

        if (fileName.endsWith(".csv")) {
            processCSV(file, dealerId);
        } else if (fileName.endsWith(".xlsx")) {
            processExcel(file, dealerId);
        } else {
            throw new Exception("Unsupported file type! Please upload CSV or XLSX.");
        }
    }

    private void processCSV(MultipartFile file, Long dealerId) throws Exception {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            String line;
            br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                PendingBook pb = new PendingBook();
                pb.setTitle(data[0].trim());
                pb.setAuthor(data[1].trim());
                pb.setGenre(data[2].trim());
                pb.setPrice(Double.parseDouble(data[3].trim()));
                pb.setDealerId(dealerId);

                pendingBookRepository.save(pb); // ✅ NOW VALID
            }
        }
    }

    private void processExcel(MultipartFile file, Long dealerId) throws Exception {

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);
                if (row == null) continue;

                PendingBook pb = new PendingBook();
                pb.setTitle(row.getCell(0).getStringCellValue());
                pb.setAuthor(row.getCell(1).getStringCellValue());
                pb.setGenre(row.getCell(2).getStringCellValue());
                pb.setPrice(row.getCell(3).getNumericCellValue());
                pb.setDealerId(dealerId);

                pendingBookRepository.save(pb);
            }
        }
    }

    @Autowired
    private DealerRepository dealerRepository;

   

    @Override
    public void approvePendingBook(Long pendingId) {

        PendingBook pb = pendingBookRepository.findById(pendingId).orElse(null);
        if (pb == null) return;

        Book book = new Book();
        book.setTitle(pb.getTitle());
        book.setAuthor(pb.getAuthor());
        book.setCategory(pb.getGenre());
        book.setPrice(pb.getPrice());
        book.setDescription(pb.getDescription());
        book.setStatus("Available");

        // ✅ Link dealer if it came from dealer
        if (pb.getDealerId() != null) {
            Dealer dealer = dealerRepository.findById(pb.getDealerId()).orElse(null);
            book.setDealer(dealer);
        }

        bookRepository.save(book);
        pendingBookRepository.delete(pb);
    }

    @Override
    public void rejectPendingBook(Long pendingId) {
        pendingBookRepository.deleteById(pendingId);
    }

    public List<Book> getPendingDonationBooks() {
        return bookRepository.findByStatus("PENDING");
    }

    



    

}
