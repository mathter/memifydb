package io.github.mathter.memifydb.transaction;

/**
 * Transaction isolation levels description.
 * <table>
 *     <tr>
 *         <th>Isolation Level</th>
 *         <th>Transactions</th>
 *         <th>Dirty Reads</th>
 *         <th>Non-Repeatable Reads</th>
 *         <th>Phantom Reads</th>
 *     </tr>
 *     <tr>
 *         <td>TRANSACTION_NONE</td>
 *         <td>Not supported</td>
 *         <td>Not applicable</td>
 *         <td>Not applicable</td>
 *         <td>Not applicable</td>
 *     </tr>
 *     <tr>
 *         <td>TRANSACTION_READ_COMMITTED</td>
 *         <td>Supported</td>
 *         <td>Prevented</td>
 *         <td>Allowed</td>
 *         <td>Allowed</td>
 *     </tr>
 *     <tr>
 *         <td>TRANSACTION_READ_UNCOMMITTED</td>
 *         <td>Supported</td>
 *         <td>Allowed</td>
 *         <td>Allowed</td>
 *         <td>Allowed</td>
 *     </tr>
 *     <tr>
 *         <td>RANSACTION_REPEATABLE_READ</td>
 *         <td>Supported</td>
 *         <td>Prevented</td>
 *         <td>Prevented</td>
 *         <td>Allowed</td>
 *     </tr>
 *     <tr>
 *         <td>TRANSACTION_SERIALIZABLE</td>
 *         <td>Supported</td>
 *         <td>Prevented</td>
 *         <td>Prevented</td>
 *         <td>Prevented</td>
 *     </tr>
 * </table>
 */
public enum TransactionIsolation {
    TRANSACTION_NONE,

    TRANSACTION_READ_COMMITTED,

    TRANSACTION_READ_UNCOMMITTED,

    TRANSACTION_REPEATABLE_READ,

    TRANSACTION_SERIALIZABLE,
}