begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.dancing
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|dancing
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A generic solver for tile laying problems using Knuth's dancing link  * algorithm. It provides a very fast backtracking data structure for problems  * that can expressed as a sparse boolean matrix where the goal is to select a  * subset of the rows such that each column has exactly 1 true in it.  *   * The application gives each column a name and each row is named after the  * set of columns that it has as true. Solutions are passed back by giving the   * selected rows' names.  *   * The type parameter ColumnName is the class of application's column names.  */
end_comment

begin_class
DECL|class|DancingLinks
specifier|public
class|class
name|DancingLinks
parameter_list|<
name|ColumnName
parameter_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DancingLinks
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * A cell in the table with up/down and left/right links that form doubly    * linked lists in both directions. It also includes a link to the column    * head.    */
DECL|class|Node
specifier|private
specifier|static
class|class
name|Node
parameter_list|<
name|ColumnName
parameter_list|>
block|{
DECL|field|left
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|left
decl_stmt|;
DECL|field|right
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|right
decl_stmt|;
DECL|field|up
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|up
decl_stmt|;
DECL|field|down
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|down
decl_stmt|;
DECL|field|head
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|head
decl_stmt|;
DECL|method|Node (Node<ColumnName> l, Node<ColumnName> r, Node<ColumnName> u, Node<ColumnName> d, ColumnHeader<ColumnName> h)
name|Node
parameter_list|(
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|l
parameter_list|,
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|r
parameter_list|,
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|u
parameter_list|,
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|d
parameter_list|,
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|h
parameter_list|)
block|{
name|left
operator|=
name|l
expr_stmt|;
name|right
operator|=
name|r
expr_stmt|;
name|up
operator|=
name|u
expr_stmt|;
name|down
operator|=
name|d
expr_stmt|;
name|head
operator|=
name|h
expr_stmt|;
block|}
DECL|method|Node ()
name|Node
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Column headers record the name of the column and the number of rows that     * satisfy this column. The names are provided by the application and can     * be anything. The size is used for the heuristic for picking the next     * column to explore.    */
DECL|class|ColumnHeader
specifier|private
specifier|static
class|class
name|ColumnHeader
parameter_list|<
name|ColumnName
parameter_list|>
extends|extends
name|Node
argument_list|<
name|ColumnName
argument_list|>
block|{
DECL|field|name
name|ColumnName
name|name
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|method|ColumnHeader (ColumnName n, int s)
name|ColumnHeader
parameter_list|(
name|ColumnName
name|n
parameter_list|,
name|int
name|s
parameter_list|)
block|{
name|name
operator|=
name|n
expr_stmt|;
name|size
operator|=
name|s
expr_stmt|;
name|head
operator|=
name|this
expr_stmt|;
block|}
DECL|method|ColumnHeader ()
name|ColumnHeader
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The head of the table. Left/Right from the head are the unsatisfied     * ColumnHeader objects.    */
DECL|field|head
specifier|private
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|head
decl_stmt|;
comment|/**    * The complete list of columns.    */
DECL|field|columns
specifier|private
name|List
argument_list|<
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|columns
decl_stmt|;
DECL|method|DancingLinks ()
specifier|public
name|DancingLinks
parameter_list|()
block|{
name|head
operator|=
operator|new
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|head
operator|.
name|left
operator|=
name|head
expr_stmt|;
name|head
operator|.
name|right
operator|=
name|head
expr_stmt|;
name|head
operator|.
name|up
operator|=
name|head
expr_stmt|;
name|head
operator|.
name|down
operator|=
name|head
expr_stmt|;
name|columns
operator|=
operator|new
name|ArrayList
argument_list|<
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a column to the table    * @param name The name of the column, which will be returned as part of     *             solutions    * @param primary Is the column required for a solution?    */
DECL|method|addColumn (ColumnName name, boolean primary)
specifier|public
name|void
name|addColumn
parameter_list|(
name|ColumnName
name|name
parameter_list|,
name|boolean
name|primary
parameter_list|)
block|{
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|top
init|=
operator|new
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|top
operator|.
name|up
operator|=
name|top
expr_stmt|;
name|top
operator|.
name|down
operator|=
name|top
expr_stmt|;
if|if
condition|(
name|primary
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|tail
init|=
name|head
operator|.
name|left
decl_stmt|;
name|tail
operator|.
name|right
operator|=
name|top
expr_stmt|;
name|top
operator|.
name|left
operator|=
name|tail
expr_stmt|;
name|top
operator|.
name|right
operator|=
name|head
expr_stmt|;
name|head
operator|.
name|left
operator|=
name|top
expr_stmt|;
block|}
else|else
block|{
name|top
operator|.
name|left
operator|=
name|top
expr_stmt|;
name|top
operator|.
name|right
operator|=
name|top
expr_stmt|;
block|}
name|columns
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a column to the table    * @param name The name of the column, which will be included in the solution    */
DECL|method|addColumn (ColumnName name)
specifier|public
name|void
name|addColumn
parameter_list|(
name|ColumnName
name|name
parameter_list|)
block|{
name|addColumn
argument_list|(
name|name
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of columns.    * @return the number of columns    */
DECL|method|getNumberColumns ()
specifier|public
name|int
name|getNumberColumns
parameter_list|()
block|{
return|return
name|columns
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Get the name of a given column as a string    * @param index the index of the column    * @return a string representation of the name    */
DECL|method|getColumnName (int index)
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|columns
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|name
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Add a row to the table.     * @param values the columns that are satisfied by this row    */
DECL|method|addRow (boolean[] values)
specifier|public
name|void
name|addRow
parameter_list|(
name|boolean
index|[]
name|values
parameter_list|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|prev
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
condition|)
block|{
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|top
init|=
name|columns
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|top
operator|.
name|size
operator|+=
literal|1
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|bottom
init|=
name|top
operator|.
name|up
decl_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
operator|new
name|Node
argument_list|<
name|ColumnName
argument_list|>
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|bottom
argument_list|,
name|top
argument_list|,
name|top
argument_list|)
decl_stmt|;
name|bottom
operator|.
name|down
operator|=
name|node
expr_stmt|;
name|top
operator|.
name|up
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|front
init|=
name|prev
operator|.
name|right
decl_stmt|;
name|node
operator|.
name|left
operator|=
name|prev
expr_stmt|;
name|node
operator|.
name|right
operator|=
name|front
expr_stmt|;
name|prev
operator|.
name|right
operator|=
name|node
expr_stmt|;
name|front
operator|.
name|left
operator|=
name|node
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|left
operator|=
name|node
expr_stmt|;
name|node
operator|.
name|right
operator|=
name|node
expr_stmt|;
block|}
name|prev
operator|=
name|node
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Applications should implement this to receive the solutions to their     * problems.    */
DECL|interface|SolutionAcceptor
specifier|public
interface|interface
name|SolutionAcceptor
parameter_list|<
name|ColumnName
parameter_list|>
block|{
comment|/**      * A callback to return a solution to the application.      * @param value a List of List of ColumnNames that were satisfied by each      *              selected row      */
DECL|method|solution (List<List<ColumnName>> value)
name|void
name|solution
parameter_list|(
name|List
argument_list|<
name|List
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|value
parameter_list|)
function_decl|;
block|}
comment|/**    * Find the column with the fewest choices.    * @return The column header    */
DECL|method|findBestColumn ()
specifier|private
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|findBestColumn
parameter_list|()
block|{
name|int
name|lowSize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|current
init|=
operator|(
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
operator|)
name|head
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|current
operator|!=
name|head
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|size
operator|<
name|lowSize
condition|)
block|{
name|lowSize
operator|=
name|current
operator|.
name|size
expr_stmt|;
name|result
operator|=
name|current
expr_stmt|;
block|}
name|current
operator|=
operator|(
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
operator|)
name|current
operator|.
name|right
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Hide a column in the table    * @param col the column to hide    */
DECL|method|coverColumn (ColumnHeader<ColumnName> col)
specifier|private
name|void
name|coverColumn
parameter_list|(
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|col
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"cover "
operator|+
name|col
operator|.
name|head
operator|.
name|name
argument_list|)
expr_stmt|;
comment|// remove the column
name|col
operator|.
name|right
operator|.
name|left
operator|=
name|col
operator|.
name|left
expr_stmt|;
name|col
operator|.
name|left
operator|.
name|right
operator|=
name|col
operator|.
name|right
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
init|=
name|col
operator|.
name|down
decl_stmt|;
while|while
condition|(
name|row
operator|!=
name|col
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|node
operator|.
name|down
operator|.
name|up
operator|=
name|node
operator|.
name|up
expr_stmt|;
name|node
operator|.
name|up
operator|.
name|down
operator|=
name|node
operator|.
name|down
expr_stmt|;
name|node
operator|.
name|head
operator|.
name|size
operator|-=
literal|1
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|right
expr_stmt|;
block|}
name|row
operator|=
name|row
operator|.
name|down
expr_stmt|;
block|}
block|}
comment|/**    * Uncover a column that was hidden.    * @param col the column to unhide    */
DECL|method|uncoverColumn (ColumnHeader<ColumnName> col)
specifier|private
name|void
name|uncoverColumn
parameter_list|(
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|col
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"uncover "
operator|+
name|col
operator|.
name|head
operator|.
name|name
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
init|=
name|col
operator|.
name|up
decl_stmt|;
while|while
condition|(
name|row
operator|!=
name|col
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|left
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|node
operator|.
name|head
operator|.
name|size
operator|+=
literal|1
expr_stmt|;
name|node
operator|.
name|down
operator|.
name|up
operator|=
name|node
expr_stmt|;
name|node
operator|.
name|up
operator|.
name|down
operator|=
name|node
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|left
expr_stmt|;
block|}
name|row
operator|=
name|row
operator|.
name|up
expr_stmt|;
block|}
name|col
operator|.
name|right
operator|.
name|left
operator|=
name|col
expr_stmt|;
name|col
operator|.
name|left
operator|.
name|right
operator|=
name|col
expr_stmt|;
block|}
comment|/**    * Get the name of a row by getting the list of column names that it     * satisfies.    * @param row the row to make a name for    * @return the list of column names    */
DECL|method|getRowName (Node<ColumnName> row)
specifier|private
name|List
argument_list|<
name|ColumnName
argument_list|>
name|getRowName
parameter_list|(
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
parameter_list|)
block|{
name|List
argument_list|<
name|ColumnName
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|ColumnName
argument_list|>
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|row
operator|.
name|head
operator|.
name|name
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|node
operator|.
name|head
operator|.
name|name
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|right
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Find a solution to the problem.    * @param partial a temporary datastructure to keep the current partial     *                answer in    * @param output the acceptor for the results that are found    * @return the number of solutions found    */
DECL|method|search (List<Node<ColumnName>> partial, SolutionAcceptor<ColumnName> output)
specifier|private
name|int
name|search
parameter_list|(
name|List
argument_list|<
name|Node
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|partial
parameter_list|,
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
name|output
parameter_list|)
block|{
name|int
name|results
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|head
operator|.
name|right
operator|==
name|head
condition|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
argument_list|(
name|partial
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
range|:
name|partial
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|getRowName
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|solution
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|results
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|col
init|=
name|findBestColumn
argument_list|()
decl_stmt|;
if|if
condition|(
name|col
operator|.
name|size
operator|>
literal|0
condition|)
block|{
name|coverColumn
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
init|=
name|col
operator|.
name|down
decl_stmt|;
while|while
condition|(
name|row
operator|!=
name|col
condition|)
block|{
name|partial
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|coverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|right
expr_stmt|;
block|}
name|results
operator|+=
name|search
argument_list|(
name|partial
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|partial
operator|.
name|remove
argument_list|(
name|partial
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|node
operator|=
name|row
operator|.
name|left
expr_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|uncoverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|left
expr_stmt|;
block|}
name|row
operator|=
name|row
operator|.
name|down
expr_stmt|;
block|}
name|uncoverColumn
argument_list|(
name|col
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
comment|/**    * Generate a list of prefixes down to a given depth. Assumes that the     * problem is always deeper than depth.    * @param depth the depth to explore down    * @param choices an array of length depth to describe a prefix    * @param prefixes a working datastructure    */
DECL|method|searchPrefixes (int depth, int[] choices, List<int[]> prefixes)
specifier|private
name|void
name|searchPrefixes
parameter_list|(
name|int
name|depth
parameter_list|,
name|int
index|[]
name|choices
parameter_list|,
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|prefixes
parameter_list|)
block|{
if|if
condition|(
name|depth
operator|==
literal|0
condition|)
block|{
name|prefixes
operator|.
name|add
argument_list|(
name|choices
operator|.
name|clone
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|col
init|=
name|findBestColumn
argument_list|()
decl_stmt|;
if|if
condition|(
name|col
operator|.
name|size
operator|>
literal|0
condition|)
block|{
name|coverColumn
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
init|=
name|col
operator|.
name|down
decl_stmt|;
name|int
name|rowId
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|row
operator|!=
name|col
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|coverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|right
expr_stmt|;
block|}
name|choices
index|[
name|choices
operator|.
name|length
operator|-
name|depth
index|]
operator|=
name|rowId
expr_stmt|;
name|searchPrefixes
argument_list|(
name|depth
operator|-
literal|1
argument_list|,
name|choices
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|node
operator|=
name|row
operator|.
name|left
expr_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|uncoverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|left
expr_stmt|;
block|}
name|row
operator|=
name|row
operator|.
name|down
expr_stmt|;
name|rowId
operator|+=
literal|1
expr_stmt|;
block|}
name|uncoverColumn
argument_list|(
name|col
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Generate a list of row choices to cover the first moves.    * @param depth the length of the prefixes to generate    * @return a list of integer arrays that list the rows to pick in order    */
DECL|method|split (int depth)
specifier|public
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|split
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
name|int
index|[]
name|choices
init|=
operator|new
name|int
index|[
name|depth
index|]
decl_stmt|;
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|int
index|[]
argument_list|>
argument_list|(
literal|100000
argument_list|)
decl_stmt|;
name|searchPrefixes
argument_list|(
name|depth
argument_list|,
name|choices
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Make one move from a prefix    * @param goalRow the row that should be chosen    * @return the row that was found    */
DECL|method|advance (int goalRow)
specifier|private
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|advance
parameter_list|(
name|int
name|goalRow
parameter_list|)
block|{
name|ColumnHeader
argument_list|<
name|ColumnName
argument_list|>
name|col
init|=
name|findBestColumn
argument_list|()
decl_stmt|;
if|if
condition|(
name|col
operator|.
name|size
operator|>
literal|0
condition|)
block|{
name|coverColumn
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
init|=
name|col
operator|.
name|down
decl_stmt|;
name|int
name|id
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|row
operator|!=
name|col
condition|)
block|{
if|if
condition|(
name|id
operator|==
name|goalRow
condition|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|right
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|coverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|right
expr_stmt|;
block|}
return|return
name|row
return|;
block|}
name|id
operator|+=
literal|1
expr_stmt|;
name|row
operator|=
name|row
operator|.
name|down
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Undo a prefix exploration    * @param row    */
DECL|method|rollback (Node<ColumnName> row)
specifier|private
name|void
name|rollback
parameter_list|(
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|row
parameter_list|)
block|{
name|Node
argument_list|<
name|ColumnName
argument_list|>
name|node
init|=
name|row
operator|.
name|left
decl_stmt|;
while|while
condition|(
name|node
operator|!=
name|row
condition|)
block|{
name|uncoverColumn
argument_list|(
name|node
operator|.
name|head
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|left
expr_stmt|;
block|}
name|uncoverColumn
argument_list|(
name|row
operator|.
name|head
argument_list|)
expr_stmt|;
block|}
comment|/**    * Given a prefix, find solutions under it.    * @param prefix a list of row choices that control which part of the search    *               tree to explore    * @param output the output for each solution    * @return the number of solutions    */
DECL|method|solve (int[] prefix, SolutionAcceptor<ColumnName> output)
specifier|public
name|int
name|solve
parameter_list|(
name|int
index|[]
name|prefix
parameter_list|,
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
name|output
parameter_list|)
block|{
name|List
argument_list|<
name|Node
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|choices
init|=
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|choices
operator|.
name|add
argument_list|(
name|advance
argument_list|(
name|prefix
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|result
init|=
name|search
argument_list|(
name|choices
argument_list|,
name|output
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|prefix
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
name|rollback
argument_list|(
name|choices
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Solve a complete problem    * @param output the acceptor to receive answers    * @return the number of solutions    */
DECL|method|solve (SolutionAcceptor<ColumnName> output)
specifier|public
name|int
name|solve
parameter_list|(
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
name|output
parameter_list|)
block|{
return|return
name|search
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|Node
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
argument_list|()
argument_list|,
name|output
argument_list|)
return|;
block|}
block|}
end_class

end_unit

