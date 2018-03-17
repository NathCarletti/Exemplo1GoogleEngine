package br.com.caroltr.gae_exemplo1.controller;

import br.com.caroltr.gae_exemplo1.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(path="/api/products")
public class ProductController {

  /*  @GetMapping("/{code}")
    public ResponseEntity<Product> getProduct(@PathVariable  int code){
        Product product = createProduct(code);
        return new	ResponseEntity<Product>(product, 	HttpStatus.OK);

    }*/

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
    @PostMapping
    public	ResponseEntity<Product>	saveProduct(@RequestBody Product product) {
        DatastoreService datastore	=	DatastoreServiceFactory.getDatastoreService();
        Key productKey	= KeyFactory.createKey("Products","productKey");
        Entity	productEntity	=	new	Entity("Products",	productKey);
        this.productToEntity	(product,	productEntity);
        datastore.put(productEntity);
        product.setId(productEntity.getKey().getId());
        return new	ResponseEntity<Product>(product, HttpStatus.CREATED);
}

  /*  @PutMapping(path = "/{code}")
    public	ResponseEntity<Product>	updateProduct(@RequestBody	Product product,
                                                       @PathVariable("code") int	code){
        product.setProductID(Integer.toString(product.getCode()));
        product.setName("New name");
        return new	ResponseEntity<Product>(product,	HttpStatus.OK);
    }*/

    @PutMapping(path="/{code}")
    public	ResponseEntity<Product>	updateProduct(@RequestBody	Product product,
                                                       @PathVariable("code")int	code)	{
        DatastoreService datastore	= DatastoreServiceFactory.getDatastoreService();
        Query.Filter	codeFilter	=	new	Query.FilterPredicate("Code", Query.FilterOperator.EQUAL, code);
        Query	query	=	new	Query("Products").setFilter(codeFilter);
        Entity	productEntity	=	datastore.prepare(query).asSingleEntity();
        if	(productEntity	!=	null)	{
            productToEntity	(product,	productEntity);
            datastore.put(productEntity);
            product.setId(productEntity.getKey().getId());
            return new	ResponseEntity<Product>(product,	HttpStatus.OK
            );
        }	else	{
            return new	ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
/*
    @DeleteMapping(path	= "/{code}")
    public	ResponseEntity<Product>	deleteProduct(@PathVariable("code") int	code)	{
        Product	product	=	createProduct(code);
        return new	ResponseEntity<Product>(product,HttpStatus.OK);
    }
*/

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

    private	Product	entityToProduct	(Entity	productEntity) {
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
