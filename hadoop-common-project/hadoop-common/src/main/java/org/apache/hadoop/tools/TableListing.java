begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_comment
comment|/**  * This class implements a "table listing" with column headers.  *  * Example:  *  * NAME   OWNER   GROUP   MODE       WEIGHT  * pool1  andrew  andrew  rwxr-xr-x     100  * pool2  andrew  andrew  rwxr-xr-x     100  * pool3  andrew  andrew  rwxr-xr-x     100  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|TableListing
specifier|public
class|class
name|TableListing
block|{
DECL|enum|Justification
specifier|public
enum|enum
name|Justification
block|{
DECL|enumConstant|LEFT
name|LEFT
block|,
DECL|enumConstant|RIGHT
name|RIGHT
block|;   }
DECL|class|Column
specifier|private
specifier|static
class|class
name|Column
block|{
DECL|field|rows
specifier|private
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rows
decl_stmt|;
DECL|field|justification
specifier|private
specifier|final
name|Justification
name|justification
decl_stmt|;
DECL|field|wrap
specifier|private
specifier|final
name|boolean
name|wrap
decl_stmt|;
DECL|field|wrapWidth
specifier|private
name|int
name|wrapWidth
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|maxWidth
specifier|private
name|int
name|maxWidth
decl_stmt|;
DECL|method|Column (String title, Justification justification, boolean wrap)
name|Column
parameter_list|(
name|String
name|title
parameter_list|,
name|Justification
name|justification
parameter_list|,
name|boolean
name|wrap
parameter_list|)
block|{
name|this
operator|.
name|rows
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|justification
operator|=
name|justification
expr_stmt|;
name|this
operator|.
name|wrap
operator|=
name|wrap
expr_stmt|;
name|this
operator|.
name|maxWidth
operator|=
literal|0
expr_stmt|;
name|addRow
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
DECL|method|addRow (String val)
specifier|private
name|void
name|addRow
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|val
operator|.
name|length
argument_list|()
operator|+
literal|1
operator|)
operator|>
name|maxWidth
condition|)
block|{
name|maxWidth
operator|=
name|val
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
block|}
comment|// Ceiling at wrapWidth, because it'll get wrapped
if|if
condition|(
name|maxWidth
operator|>
name|wrapWidth
condition|)
block|{
name|maxWidth
operator|=
name|wrapWidth
expr_stmt|;
block|}
name|rows
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
DECL|method|getMaxWidth ()
specifier|private
name|int
name|getMaxWidth
parameter_list|()
block|{
return|return
name|maxWidth
return|;
block|}
DECL|method|setWrapWidth (int width)
specifier|private
name|void
name|setWrapWidth
parameter_list|(
name|int
name|width
parameter_list|)
block|{
name|wrapWidth
operator|=
name|width
expr_stmt|;
comment|// Ceiling the maxLength at wrapWidth
if|if
condition|(
name|maxWidth
operator|>
name|wrapWidth
condition|)
block|{
name|maxWidth
operator|=
name|wrapWidth
expr_stmt|;
block|}
comment|// Else we need to traverse through and find the real maxWidth
else|else
block|{
name|maxWidth
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rows
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|length
init|=
name|rows
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|>
name|maxWidth
condition|)
block|{
name|maxWidth
operator|=
name|length
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Return the ith row of the column as a set of wrapped strings, each at      * most wrapWidth in length.      */
DECL|method|getRow (int idx)
name|String
index|[]
name|getRow
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
name|String
name|raw
init|=
name|rows
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
comment|// Line-wrap if it's too long
name|String
index|[]
name|lines
init|=
operator|new
name|String
index|[]
block|{
name|raw
block|}
decl_stmt|;
if|if
condition|(
name|wrap
condition|)
block|{
name|lines
operator|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
operator|.
name|wrap
argument_list|(
name|lines
index|[
literal|0
index|]
argument_list|,
name|wrapWidth
argument_list|,
literal|"\n"
argument_list|,
literal|true
argument_list|)
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lines
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|justification
operator|==
name|Justification
operator|.
name|LEFT
condition|)
block|{
name|lines
index|[
name|i
index|]
operator|=
name|StringUtils
operator|.
name|rightPad
argument_list|(
name|lines
index|[
name|i
index|]
argument_list|,
name|maxWidth
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|justification
operator|==
name|Justification
operator|.
name|RIGHT
condition|)
block|{
name|lines
index|[
name|i
index|]
operator|=
name|StringUtils
operator|.
name|leftPad
argument_list|(
name|lines
index|[
name|i
index|]
argument_list|,
name|maxWidth
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|lines
return|;
block|}
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|columns
specifier|private
specifier|final
name|LinkedList
argument_list|<
name|Column
argument_list|>
name|columns
init|=
operator|new
name|LinkedList
argument_list|<
name|Column
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|showHeader
specifier|private
name|boolean
name|showHeader
init|=
literal|true
decl_stmt|;
DECL|field|wrapWidth
specifier|private
name|int
name|wrapWidth
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**      * Create a new Builder.      */
DECL|method|Builder ()
specifier|public
name|Builder
parameter_list|()
block|{     }
DECL|method|addField (String title)
specifier|public
name|Builder
name|addField
parameter_list|(
name|String
name|title
parameter_list|)
block|{
return|return
name|addField
argument_list|(
name|title
argument_list|,
name|Justification
operator|.
name|LEFT
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|addField (String title, Justification justification)
specifier|public
name|Builder
name|addField
parameter_list|(
name|String
name|title
parameter_list|,
name|Justification
name|justification
parameter_list|)
block|{
return|return
name|addField
argument_list|(
name|title
argument_list|,
name|justification
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|addField (String title, boolean wrap)
specifier|public
name|Builder
name|addField
parameter_list|(
name|String
name|title
parameter_list|,
name|boolean
name|wrap
parameter_list|)
block|{
return|return
name|addField
argument_list|(
name|title
argument_list|,
name|Justification
operator|.
name|LEFT
argument_list|,
name|wrap
argument_list|)
return|;
block|}
comment|/**      * Add a new field to the Table under construction.      *      * @param title Field title.      * @param justification Right or left justification. Defaults to left.      * @param wrap Width at which to auto-wrap the content of the cell.      *        Defaults to Integer.MAX_VALUE.      * @return This Builder object      */
DECL|method|addField (String title, Justification justification, boolean wrap)
specifier|public
name|Builder
name|addField
parameter_list|(
name|String
name|title
parameter_list|,
name|Justification
name|justification
parameter_list|,
name|boolean
name|wrap
parameter_list|)
block|{
name|columns
operator|.
name|add
argument_list|(
operator|new
name|Column
argument_list|(
name|title
argument_list|,
name|justification
argument_list|,
name|wrap
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Whether to hide column headers in table output      */
DECL|method|hideHeaders ()
specifier|public
name|Builder
name|hideHeaders
parameter_list|()
block|{
name|this
operator|.
name|showHeader
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Whether to show column headers in table output. This is the default.      */
DECL|method|showHeaders ()
specifier|public
name|Builder
name|showHeaders
parameter_list|()
block|{
name|this
operator|.
name|showHeader
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the maximum width of a row in the TableListing. Must have one or      * more wrappable fields for this to take effect.      */
DECL|method|wrapWidth (int width)
specifier|public
name|Builder
name|wrapWidth
parameter_list|(
name|int
name|width
parameter_list|)
block|{
name|this
operator|.
name|wrapWidth
operator|=
name|width
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Create a new TableListing.      */
DECL|method|build ()
specifier|public
name|TableListing
name|build
parameter_list|()
block|{
return|return
operator|new
name|TableListing
argument_list|(
name|columns
operator|.
name|toArray
argument_list|(
operator|new
name|Column
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|showHeader
argument_list|,
name|wrapWidth
argument_list|)
return|;
block|}
block|}
DECL|field|columns
specifier|private
specifier|final
name|Column
name|columns
index|[]
decl_stmt|;
DECL|field|numRows
specifier|private
name|int
name|numRows
decl_stmt|;
DECL|field|showHeader
specifier|private
specifier|final
name|boolean
name|showHeader
decl_stmt|;
DECL|field|wrapWidth
specifier|private
specifier|final
name|int
name|wrapWidth
decl_stmt|;
DECL|method|TableListing (Column columns[], boolean showHeader, int wrapWidth)
name|TableListing
parameter_list|(
name|Column
name|columns
index|[]
parameter_list|,
name|boolean
name|showHeader
parameter_list|,
name|int
name|wrapWidth
parameter_list|)
block|{
name|this
operator|.
name|columns
operator|=
name|columns
expr_stmt|;
name|this
operator|.
name|numRows
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|showHeader
operator|=
name|showHeader
expr_stmt|;
name|this
operator|.
name|wrapWidth
operator|=
name|wrapWidth
expr_stmt|;
block|}
comment|/**    * Add a new row.    *    * @param row    The row of objects to add-- one per column.    */
DECL|method|addRow (String... row)
specifier|public
name|void
name|addRow
parameter_list|(
name|String
modifier|...
name|row
parameter_list|)
block|{
if|if
condition|(
name|row
operator|.
name|length
operator|!=
name|columns
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"trying to add a row with "
operator|+
name|row
operator|.
name|length
operator|+
literal|" columns, but we have "
operator|+
name|columns
operator|.
name|length
operator|+
literal|" columns."
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|columns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|columns
index|[
name|i
index|]
operator|.
name|addRow
argument_list|(
name|row
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|numRows
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Calculate the widths of each column based on their maxWidths and
comment|// the wrapWidth for the entire table
name|int
name|width
init|=
operator|(
name|columns
operator|.
name|length
operator|-
literal|1
operator|)
operator|*
literal|2
decl_stmt|;
comment|// inter-column padding
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|columns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|width
operator|+=
name|columns
index|[
name|i
index|]
operator|.
name|maxWidth
expr_stmt|;
block|}
comment|// Decrease the column size of wrappable columns until the goal width
comment|// is reached, or we can't decrease anymore
while|while
condition|(
name|width
operator|>
name|wrapWidth
condition|)
block|{
name|boolean
name|modified
init|=
literal|false
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
name|columns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Column
name|column
init|=
name|columns
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|column
operator|.
name|wrap
condition|)
block|{
name|int
name|maxWidth
init|=
name|column
operator|.
name|getMaxWidth
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxWidth
operator|>
literal|10
condition|)
block|{
name|column
operator|.
name|setWrapWidth
argument_list|(
name|maxWidth
operator|-
literal|1
argument_list|)
expr_stmt|;
name|modified
operator|=
literal|true
expr_stmt|;
name|width
operator|-=
literal|1
expr_stmt|;
if|if
condition|(
name|width
operator|<=
name|wrapWidth
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|modified
condition|)
block|{
break|break;
block|}
block|}
name|int
name|startrow
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|showHeader
condition|)
block|{
name|startrow
operator|=
literal|1
expr_stmt|;
block|}
name|String
index|[]
index|[]
name|columnLines
init|=
operator|new
name|String
index|[
name|columns
operator|.
name|length
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|startrow
init|;
name|i
operator|<
name|numRows
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|int
name|maxColumnLines
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|columns
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|columnLines
index|[
name|j
index|]
operator|=
name|columns
index|[
name|j
index|]
operator|.
name|getRow
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|columnLines
index|[
name|j
index|]
operator|.
name|length
operator|>
name|maxColumnLines
condition|)
block|{
name|maxColumnLines
operator|=
name|columnLines
index|[
name|j
index|]
operator|.
name|length
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|maxColumnLines
condition|;
name|c
operator|++
control|)
block|{
comment|// First column gets no left-padding
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|columns
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
comment|// Prepend padding
name|builder
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|" "
expr_stmt|;
if|if
condition|(
name|columnLines
index|[
name|j
index|]
operator|.
name|length
operator|>
name|c
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|columnLines
index|[
name|j
index|]
index|[
name|c
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|" "
argument_list|,
name|columns
index|[
name|j
index|]
operator|.
name|maxWidth
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

