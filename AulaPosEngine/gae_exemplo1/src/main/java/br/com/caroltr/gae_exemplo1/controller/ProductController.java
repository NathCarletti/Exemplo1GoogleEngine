package br.com.caroltr.gae_exemplo1.controller;

import br.com.caroltr.gae_exemplo1.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@RestController
@RequestMapping(path="/api/products")
public class ProductController {
    private static final	Logger	log	= Logger.getLogger("ProductController");
  /*  @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable  int code){
        Product product = createProduct(code);
        return new	ResponseEntity<Product>(product, 	HttpStatus.OK);

    }*/
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping
    public	ResponseEntity<List<Product>>getProducts()	{
        List<Product>	products	=	new	ArrayList<>();
        DatastoreService	datastore	=	DatastoreServiceFactory
                .getDatastoreService();
        Query	query;
        query	=	new	Query("Products").addSort("Code",
                Query.SortDirection.ASCENDING);
        List<Entity>	productsEntities	=	datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        for	(Entity	productEntity	:	productsEntities)	{
            Product	product	=	entityToProduct(productEntity);
            products.add(product);
        }
        return new	ResponseEntity<List<Product>>(products,	HttpStatus.OK);
    }
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/{code}")
    public	ResponseEntity<Product>	getProduct(@PathVariable	int	code){
        DatastoreService datastore	= DatastoreServiceFactory
                .getDatastoreService();
        Query.Filter	codeFilter	=	new	Query.FilterPredicate("Code", Query.FilterOperator.EQUAL,	code);
        Query	query	=	new	Query("Products").setFilter(codeFilter);
        Entity	productEntity	=	datastore.prepare(query).asSingleEntity();
        if	(productEntity	!=	null)	{
            Product	product	=	entityToProduct(productEntity);
            return new	ResponseEntity<Product>(product,	HttpStatus.OK
            );
        }	else	{
            return new	ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
/*
    @GetMapping
    public	ResponseEntity<List<Product>>getProducts()	{
        List<Product>products =	new ArrayList<>();
        for	(int	j	=	1;	j	<=	5;	j++)	{
            products.add(createProduct(j));
        }
        return new	ResponseEntity<List<Product>>(products,	HttpStatus.OK);
    }
/*
    @PostMapping
    public	ResponseEntity<Product>	saveProduct(@RequestBody Product product) {
        product.setProductID(Integer.toString(product.getCode()));
        return new	ResponseEntity<Product>(product, HttpStatus.CREATED);
    }*/
    //Preenche BD
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public	ResponseEntity<Product>	saveProduct(@RequestBody Product product) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        if (!checkIfCodeExist (product)) {
            Key productKey = KeyFactory.createKey("Products", "productKey");
            Entity productEntity = new Entity("Products", productKey);

            productToEntity (product, productEntity);

            datastore.put(productEntity);

            product.setId(productEntity.getKey().getId());
        } else {
            return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Product>(product, HttpStatus.CREATED);
}

  /*  @PutMapping(path = "/{code}")
    public	ResponseEntity<Product>	updateProduct(@RequestBody	Product product,
                                                       @PathVariable("code") int	code){
        product.setProductID(Integer.toString(product.getCode()));
        product.setName("New name");
        return new	ResponseEntity<Product>(product,	HttpStatus.OK);
    }*/
  @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(path="/{code}")
    public	ResponseEntity<Product>	updateProduct(@RequestBody	Product product,
                                                       @PathVariable("code")int	code)	{
        if(code<=0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else {
            if (!checkIfCodeExist (product)) {
                DatastoreService datastore = DatastoreServiceFactory
                        .getDatastoreService();

                Query.Filter codeFilter = new Query.FilterPredicate("Code",
                        Query.FilterOperator.EQUAL, code);

                Query query = new Query("Products").setFilter(codeFilter);

                Entity productEntity = datastore.prepare(query).asSingleEntity();

                if (productEntity != null) {
                    productToEntity (product, productEntity);

                    datastore.put(productEntity);

                    product.setId(productEntity.getKey().getId());

                    return new ResponseEntity<Product>(product, HttpStatus.OK);
                } else {
                    return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
            }
        }
    }
/*
    @DeleteMapping(path	= "/{code}")
    public	ResponseEntity<Product>	deleteProduct(@PathVariable("code") int	code)	{
        Product	product	=	createProduct(code);
        return new	ResponseEntity<Product>(product,HttpStatus.OK);
    }
*/
@PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping(path	=	"/{code}")
    public	ResponseEntity<Product>	deleteProduct(@PathVariable("code")	int	code) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query.Filter codeFilter = new Query.FilterPredicate("Code",
                Query.FilterOperator.EQUAL, code);
        Query query = new Query("Products").setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();
        if (productEntity != null) {
            datastore.delete(productEntity.getKey());
            Product product = entityToProduct(productEntity);
            return new ResponseEntity<Product>(product, HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    private	void	productToEntity	(Product product, Entity productEntity) {
        productEntity.setProperty("ProductID",	product.getProductID());
        productEntity.setProperty("Name",	product.getName());
        productEntity.setProperty("Code",	product.getCode());
        productEntity.setProperty("Model",	product.getModel());
        productEntity.setProperty("Price",	product.getPrice());
    }
    private boolean checkIfCodeExist (Product product) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate("Code",
                Query.FilterOperator.EQUAL, product.getCode());

        Query query = new Query("Products").setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity == null) {
            return false;
        } else {
            if (productEntity.getKey().getId() == product.getId()) {
                //está alterando o mesmo produto
                return false;
            } else {
                return true;
            }
        }
    }

    public static Product entityToProduct(Entity productEntity) {
        Product	product	=	new	Product();
        product.setId(productEntity.getKey().getId());
        product.setProductID((String)	productEntity.getProperty("ProductID"));
                product.setName((String)productEntity.getProperty("Name"));
        product.setCode(Integer.parseInt(productEntity.getProperty("Code")
                        .toString()));
        product.setModel((String)productEntity.getProperty("Model"))
        ;
        product.setPrice(Float.parseFloat(productEntity.getProperty("Price").toString()));
        return	product;
    }



    private	Product	createProduct	(int	code) {
        Product	product	=	new	Product();
        product.setProductID(Integer.toString(code));
        product.setCode(code);
        product.setModel("Model	"	+	code);
        product.setName("Name	"	+	code);
        product.setPrice(10	*	code);
        return	product;
    }

}
