package id.ac.ui.cs.advprog.productservice.model;

import id.ac.ui.cs.advprog.productservice.productmanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);

        // Configure the mock product
        when(mockProduct.getPrice()).thenReturn(PRODUCT_PRICE);

        // Create transaction item with the mock product
        transactionItem = new TransactionItem(mockProduct, INITIAL_QUANTITY);
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        // Assert
        assertEquals(mockProduct, transactionItem.getProduct());
        assertEquals(INITIAL_QUANTITY, transactionItem.getQuantity());
        assertEquals(PRODUCT_PRICE * INITIAL_QUANTITY, transactionItem.getSubtotal());
        assertNull(transactionItem.getTransaction());

        // Verify that getPrice was called during construction
        verify(mockProduct).getPrice();
    }

    @Test
    void updateQuantity_ShouldUpdateQuantityAndSubtotal() {
        // Arrange
        int newQuantity = 5;
        double expectedSubtotal = PRODUCT_PRICE * newQuantity;

        // Act
        transactionItem.updateQuantity(newQuantity);

        // Assert
        assertEquals(newQuantity, transactionItem.getQuantity());
        assertEquals(expectedSubtotal, transactionItem.getSubtotal());

        // Verify getPrice was called again
        verify(mockProduct, times(2)).getPrice(); // Once in constructor, once in updateQuantity
    }

    @Test
    void setTransaction_ShouldSetTransactionCorrectly() {
        // Act
        transactionItem.setTransaction(mockTransaction);

        // Assert
        assertEquals(mockTransaction, transactionItem.getTransaction());
    }

    @Test
    void setProduct_ShouldSetProductCorrectly() {
        // Arrange
        Product newProduct = mock(Product.class);

        // Act
        transactionItem.setProduct(newProduct);

        // Assert
        assertEquals(newProduct, transactionItem.getProduct());
    }

    @Test
    void setQuantity_ShouldSetQuantityCorrectly() {
        // Arrange
        int newQuantity = 10;

        // Act
        transactionItem.setQuantity(newQuantity);

        // Assert
        assertEquals(newQuantity, transactionItem.getQuantity());
        // Note: setQuantity doesn't automatically update subtotal (only updateQuantity does)
    }

    @Test
    void setSubtotal_ShouldSetSubtotalCorrectly() {
        // Arrange
        double newSubtotal = 999.99;

        // Act
        transactionItem.setSubtotal(newSubtotal);

        // Assert
        assertEquals(newSubtotal, transactionItem.getSubtotal());
    }

    @Test
    void updateQuantity_WithZeroQuantity_ShouldUpdateSubtotalToZero() {
        // Act
        transactionItem.updateQuantity(0);

        // Assert
        assertEquals(0, transactionItem.getQuantity());
        assertEquals(0.0, transactionItem.getSubtotal());
    }

    @Test
    void updateQuantity_WithNegativeQuantity_ShouldStillUpdateButWithNegativeSubtotal() {
        // Arrange
        int negativeQuantity = -3;

        // Act
        transactionItem.updateQuantity(negativeQuantity);

        // Assert
        assertEquals(negativeQuantity, transactionItem.getQuantity());
        assertEquals(PRODUCT_PRICE * negativeQuantity, transactionItem.getSubtotal());
    }

    @Test
    void constructor_WithNullProduct_ShouldThrowNullPointerException() {
        // Assert
        assertThrows(NullPointerException.class, () -> new TransactionItem(null, INITIAL_QUANTITY));
    }

    @Test
    void updateQuantity_AfterProductPriceChange_ShouldReflectNewPrice() {
        // Arrange
        double newPrice = 200.0;
        when(mockProduct.getPrice()).thenReturn(newPrice);
        int newQuantity = 3;

        // Act
        transactionItem.updateQuantity(newQuantity);

        // Assert
        assertEquals(newPrice * newQuantity, transactionItem.getSubtotal());
    }
}