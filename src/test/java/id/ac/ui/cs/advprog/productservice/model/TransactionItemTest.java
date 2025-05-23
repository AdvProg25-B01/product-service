package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionItemTest {

    @Mock
    private Product mockProduct;

    @Mock
    private Transaction mockTransaction;

    private TransactionItem transactionItem;
    private static final double PRODUCT_PRICE = 100.0;
    private static final int INITIAL_QUANTITY = 2;

    @BeforeEach
    void setUp() {
        when(mockProduct.getPrice()).thenReturn(PRODUCT_PRICE);

        transactionItem = new TransactionItem(mockProduct, INITIAL_QUANTITY);
    }

    @Test
    void defaultConstructor_ShouldInitializeWithId() {
        TransactionItem item = new TransactionItem();

        assertNotNull(item.getId());
        assertNull(item.getProduct());
        assertEquals(0, item.getQuantity());
        assertEquals(0.0, item.getSubtotal());
        assertNull(item.getTransaction());
    }

    @Test
    void parameterizedConstructor_ShouldInitializeCorrectly() {
        assertNotNull(transactionItem.getId());
        assertEquals(mockProduct, transactionItem.getProduct());
        assertEquals(INITIAL_QUANTITY, transactionItem.getQuantity());
        assertEquals(PRODUCT_PRICE * INITIAL_QUANTITY, transactionItem.getSubtotal());
        assertNull(transactionItem.getTransaction());

        verify(mockProduct).getPrice();
    }

    @Test
    void updateQuantity_ShouldUpdateQuantityAndSubtotal() {
        int newQuantity = 5;
        double expectedSubtotal = PRODUCT_PRICE * newQuantity;

        transactionItem.updateQuantity(newQuantity);

        assertEquals(newQuantity, transactionItem.getQuantity());
        assertEquals(expectedSubtotal, transactionItem.getSubtotal());

        verify(mockProduct, times(2)).getPrice();
    }

    @Test
    void setTransaction_ShouldSetTransactionCorrectly() {
        transactionItem.setTransaction(mockTransaction);

        assertEquals(mockTransaction, transactionItem.getTransaction());
    }

    @Test
    void setProduct_ShouldSetProductCorrectly() {
        Product newProduct = mock(Product.class);

        transactionItem.setProduct(newProduct);

        assertEquals(newProduct, transactionItem.getProduct());
    }

    @Test
    void setQuantity_ShouldSetQuantityCorrectly() {
        int newQuantity = 10;

        transactionItem.setQuantity(newQuantity);

        assertEquals(newQuantity, transactionItem.getQuantity());
        assertEquals(PRODUCT_PRICE * INITIAL_QUANTITY, transactionItem.getSubtotal());
    }

    @Test
    void setSubtotal_ShouldSetSubtotalCorrectly() {
        double newSubtotal = 999.99;

        transactionItem.setSubtotal(newSubtotal);

        assertEquals(newSubtotal, transactionItem.getSubtotal());
    }

    @Test
    void updateQuantity_WithZeroQuantity_ShouldUpdateSubtotalToZero() {
        transactionItem.updateQuantity(0);

        assertEquals(0, transactionItem.getQuantity());
        assertEquals(0.0, transactionItem.getSubtotal());
    }

    @Test
    void updateQuantity_WithNegativeQuantity_ShouldStillUpdateButWithNegativeSubtotal() {
        int negativeQuantity = -3;

        transactionItem.updateQuantity(negativeQuantity);

        assertEquals(negativeQuantity, transactionItem.getQuantity());
        assertEquals(PRODUCT_PRICE * negativeQuantity, transactionItem.getSubtotal());
    }

    @Test
    void constructor_WithNullProduct_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TransactionItem(null, INITIAL_QUANTITY));
    }

    @Test
    void updateQuantity_WithNullProduct_ShouldThrowNullPointerException() {
        TransactionItem itemWithNullProduct = new TransactionItem();
        itemWithNullProduct.setProduct(null);

        assertThrows(NullPointerException.class, () -> itemWithNullProduct.updateQuantity(5));
    }

    @Test
    void updateQuantity_AfterProductPriceChange_ShouldReflectNewPrice() {
        double newPrice = 200.0;
        when(mockProduct.getPrice()).thenReturn(newPrice);
        int newQuantity = 3;

        transactionItem.updateQuantity(newQuantity);

        assertEquals(newPrice * newQuantity, transactionItem.getSubtotal());
        verify(mockProduct, times(2)).getPrice();
    }

    @Test
    void calculateSubtotal_ShouldBeCalledOnPrePersistAndPreUpdate() {
        TransactionItem item = new TransactionItem();
        item.setProduct(mockProduct);
        item.setQuantity(3);

        clearInvocations(mockProduct);

        item.calculateSubtotal();

        assertEquals(PRODUCT_PRICE * 3, item.getSubtotal());
        verify(mockProduct, times(1)).getPrice();
    }

    @Test
    void calculateSubtotal_WithNullProduct_ShouldNotThrowException() {
        TransactionItem item = new TransactionItem();
        item.setProduct(null);
        item.setQuantity(5);

        assertDoesNotThrow(() -> item.calculateSubtotal());
        assertEquals(0.0, item.getSubtotal());
    }

    @Test
    void getId_ShouldReturnUniqueIds() {
        TransactionItem item1 = new TransactionItem();
        TransactionItem item2 = new TransactionItem();

        assertNotNull(item1.getId());
        assertNotNull(item2.getId());
        assertNotEquals(item1.getId(), item2.getId());
    }
}