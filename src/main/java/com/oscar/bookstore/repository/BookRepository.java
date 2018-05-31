package com.oscar.bookstore.repository;

import com.oscar.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {
    List<Book> findByIdAndUploadUserId(Integer bookId, Integer uploadUserId);
}
