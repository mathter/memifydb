package io.github.mathter.memifydb.transaction;

/**
 * Copyright 2026 Alexander Kashirsky (mathter)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
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