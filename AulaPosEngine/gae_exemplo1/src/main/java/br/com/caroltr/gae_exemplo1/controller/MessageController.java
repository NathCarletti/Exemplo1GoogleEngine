package br.com.caroltr.gae_exemplo1.controller;


import br.com.caroltr.gae_exemplo1.Repository.UserRepository;
import br.com.caroltr.gae_exemplo1.model.Product;
import br.com.caroltr.gae_exemplo1.model.User;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping(path="/api/message")
public class MessageController {
    private static final Logger log = Logger.getLogger("MessageController");
    @Autowired
    private UserRepository userRepository;
        @PreAuthorize("hasAuthority('ADMIN')")
        @PutMapping(path = "/sendproduct")
        public ResponseEntity<String> sendProduct (
                @RequestParam("email") String email,
                @RequestParam("productCode") String productCode){
            Optional<User> optUser = userRepository.getByEmail(email);
            if (optUser.isPresent()) {
                User user = optUser.get();
                Product product = findProduct(Integer.parseInt(productCode));
                if (product != null) {
                    Sender sender = new Sender("AIzaSyB5EFkUS6z4I0OrS6MU54Z1gtdSnbFdShs");
                    Gson gson = new Gson();
                    Message message = new Message.Builder().addData("product", gson.toJson(product)).build();
                    Result result;
                    try {
                        result = sender.send(message, user.getGcmRegId(), 5);
                        if (result.getMessageId() != null) {
                            String canonicalRegId = result.getCanonicalRegistrationId();
                            if (canonicalRegId != null) {
                                log.severe("Usuário [" + user.getEmail() + "] com mais de um registro");
                            }
                        } else {
                            String error = result.getErrorCodeName();
                            log.severe("Usuário [" + user.getEmail() + "] não registrado"
                            );
                            log.severe(error);
                            return new ResponseEntity<String>("Usuário não registrado no GCM", HttpStatus.NOT_FOUND);
                        }
                    } catch (IOException e) {
                        log.severe("Falha ao enviar mensagem");
                        return new ResponseEntity<String>("Falha ao enviar a mensagem", HttpStatus.PRECONDITION_FAILED);
                    }
                    log.severe("Mensagem enviada ao produto " + product.getName());
                    return new ResponseEntity<String>("Mensagem enviada com o produto" + product.getName(), HttpStatus.OK);
                } else {
                    log.severe("Produto não encontrado");
                    return new ResponseEntity<String>("Produto não encontrado",
                            HttpStatus.NOT_FOUND);
                }
            } else {
                log.severe("Usuário não encontrado");
                return new ResponseEntity<String>("Usuário não encontrado",
                        HttpStatus.NOT_FOUND);
            }
        }


    private Product findProduct (int code) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code", Query.FilterOperator.EQUAL, code);

        Query query = new Query("Products").setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity != null) {
            return ProductController.entityToProduct(productEntity);
        } else {
            return null;
        }
    }
}

