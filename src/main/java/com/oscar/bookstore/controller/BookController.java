package com.oscar.bookstore.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oscar.bookstore.model.Customer;
import com.oscar.bookstore.model.CustomerDetail;
import com.oscar.bookstore.model.CustomerSecurity;
import com.oscar.bookstore.repository.CustomerRepository;
import com.oscar.bookstore.service.UserLoginInfo;
import com.oscar.bookstore.model.Book;
import com.oscar.bookstore.repository.BookRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping(value = "/all/{email}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, Object>> showAllBooks(@PathVariable String email, HttpServletRequest request) {

            Map<String, Object> json = new HashMap<>();
            if(email.equals("defaultUser")) {
                Customer noCustomer = new Customer();
                Customer customer = getCustomerByTokenId();
                if (customer!=null) {
                    json = getBooksFromCustomer(customer, json);
                    json = getUserDetail(customer, noCustomer, json);
                    return new ResponseEntity<>(json, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }
            } else {
                Customer defaultCustomer = getCustomerByTokenId();
                if(defaultCustomer!=null) {
                    Customer otherCustomer = customerRepository.findByEmail(email);
                    if(otherCustomer!=null) {
                        json = getBooksFromCustomer(otherCustomer, json);
                        json = getUserDetail(defaultCustomer, otherCustomer, json);
                        return new ResponseEntity<>(json, HttpStatus.OK);
                    } else {
                        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                    }
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            }
    }

    @GetMapping(value = "/detail/{bookId}/{uploadUserId}", produces = "application/json;charset=UTF-8")
    public ResponseEntity<List<Book>> showBookDetail(@PathVariable Integer bookId, @PathVariable Integer uploadUserId, HttpServletRequest request) {

        List<Book> books = bookRepository.findByIdAndUploadUserId(bookId, uploadUserId);
        if(books!=null) {
            secureData(books);
            return new ResponseEntity<>(books, HttpStatus.OK);
//
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> addNewBook(@RequestBody String jsonBook, HttpServletRequest request) {

            Customer customer = getCustomerByTokenId();

            if (customer!=null) {
                byte[] image = retrieveImage(jsonBook);
                if(image!=null) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        Book book = mapper.readValue(jsonBook, Book.class);
                        book.setUploadUserId(customer.getId());
                        book.setBookPic(image);
                        bookRepository.save(book);
                        addNewBookToCustomer(customer, book);
                        System.out.println("book saved!");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Can not convert into object");
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body("Register book success");
                } else {
                    return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Can not retrieve image form json");
                }



            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Server Error");
            }
    }

    @DeleteMapping(value = "/delete/{bookId}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteBook(@PathVariable Integer bookId, HttpServletRequest request) {
        Customer customer = getCustomerByTokenId();

        if(customer!=null) {

           bookRepository.deleteById(bookId);

           return ResponseEntity.status(HttpStatus.OK).body("Delete book success");
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Server Error");
        }


    }

    @PutMapping(value = "/update", produces = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateBook(@RequestBody Book receiveBook, HttpServletRequest request) {

        Customer customer = getCustomerByTokenId();
        if(customer!=null) {
            Book book = bookRepository.findById(receiveBook.getId()).orElse(null);
            if(book!=null) {
                book.setTitle(receiveBook.getTitle());
                book.setType(receiveBook.getType());
                book.setDate(receiveBook.getDate());
                book.setAuthor(receiveBook.getAuthor());
                book.setPrice(receiveBook.getPrice());
                book.setDescription(receiveBook.getDescription());
                book.setStatus(receiveBook.getStatus());
                bookRepository.save(book);
                return ResponseEntity.status(HttpStatus.OK).body("Update success!");
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Server Error");
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Server Error");
        }

    }

    private byte[] retrieveImage(String json) {
        JSONObject object = new JSONObject(json);
        JSONArray images = object.getJSONArray("bookPic");
        byte[] image = null;
        for ( int i = 0; i < images.length(); ++i ) {
            JSONObject temp = images.getJSONObject(i);
            String[] data = temp.getString("src").split(",");
            try {
                image = Base64.getDecoder().decode(data[1].getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return image;
    }


    private void addNewBookToCustomer(Customer customer, Book book) {
        List<Book> books = customer.getBooks();
        books.add(book);
        customer.setBooks(books);
        customerRepository.save(customer);
    }

    private Customer getCustomerByTokenId() {
        Integer userId = UserLoginInfo.getInstance().getUserId();
        if(userId!=null) {
            Customer customer = customerRepository.findById(userId).orElse(null);
            if(customer!=null) {
                return customer;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void secureData(List<Book> books) {
        List<Customer> tempCustomers;
        CustomerDetail NoCustomerDetail = new CustomerDetail();
        CustomerSecurity NoCustomerSecurity = new CustomerSecurity();

        for(Book book: books) {
            tempCustomers = book.getCustomers();
            for(Customer customer: tempCustomers) {
                customer.setCustomerDetail(NoCustomerDetail);
                customer.setCustomerSecurity(NoCustomerSecurity);
            }
            book.setCustomers(tempCustomers);
        }
    }

    private Map<String, Object> getBooksFromCustomer(Customer customer,Map<String, Object> map) {
        List<Book> books = customer.getBooks();
        map.put("books",books);
        return map;
    }

    private Map<String ,Object> getUserDetail(Customer defaultCustomer,Customer otherCustomer, Map<String, Object> map) {
        map.put("defaultUserId", defaultCustomer.getId().toString());

        if(otherCustomer.getId()==null) {
            map.put("otherUserId","");
            map.put("userName", defaultCustomer.getLastName()+" "+defaultCustomer.getFirstName());
        } else {
            map.put("otherUserId",otherCustomer.getId().toString());
            map.put("userName", otherCustomer.getLastName()+" "+otherCustomer.getFirstName());
        }

        return map;
    }

}
