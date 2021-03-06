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

begin_class
DECL|class|Pentomino
specifier|public
class|class
name|Pentomino
block|{
DECL|field|DEPTH
specifier|public
specifier|static
specifier|final
name|String
name|DEPTH
init|=
literal|"mapreduce.pentomino.depth"
decl_stmt|;
DECL|field|WIDTH
specifier|public
specifier|static
specifier|final
name|String
name|WIDTH
init|=
literal|"mapreduce.pentomino.width"
decl_stmt|;
DECL|field|HEIGHT
specifier|public
specifier|static
specifier|final
name|String
name|HEIGHT
init|=
literal|"mapreduce.pentomino.height"
decl_stmt|;
DECL|field|CLASS
specifier|public
specifier|static
specifier|final
name|String
name|CLASS
init|=
literal|"mapreduce.pentomino.class"
decl_stmt|;
comment|/**    * This interface just is a marker for what types I expect to get back    * as column names.    */
DECL|interface|ColumnName
specifier|protected
specifier|static
interface|interface
name|ColumnName
block|{
comment|// NOTHING
block|}
comment|/**    * Maintain information about a puzzle piece.    */
DECL|class|Piece
specifier|protected
specifier|static
class|class
name|Piece
implements|implements
name|ColumnName
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|shape
specifier|private
name|boolean
index|[]
index|[]
name|shape
decl_stmt|;
DECL|field|rotations
specifier|private
name|int
index|[]
name|rotations
decl_stmt|;
DECL|field|flippable
specifier|private
name|boolean
name|flippable
decl_stmt|;
DECL|method|Piece (String name, String shape, boolean flippable, int[] rotations)
specifier|public
name|Piece
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|shape
parameter_list|,
name|boolean
name|flippable
parameter_list|,
name|int
index|[]
name|rotations
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|rotations
operator|=
name|rotations
expr_stmt|;
name|this
operator|.
name|flippable
operator|=
name|flippable
expr_stmt|;
name|StringTokenizer
name|parser
init|=
operator|new
name|StringTokenizer
argument_list|(
name|shape
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|boolean
index|[]
argument_list|>
name|lines
init|=
operator|new
name|ArrayList
argument_list|<
name|boolean
index|[]
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|boolean
index|[]
name|line
init|=
operator|new
name|boolean
index|[
name|token
operator|.
name|length
argument_list|()
index|]
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
name|line
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|line
index|[
name|i
index|]
operator|=
name|token
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'x'
expr_stmt|;
block|}
name|lines
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|shape
operator|=
operator|new
name|boolean
index|[
name|lines
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|lines
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|shape
index|[
name|i
index|]
operator|=
name|lines
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getRotations ()
specifier|public
name|int
index|[]
name|getRotations
parameter_list|()
block|{
return|return
name|rotations
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getFlippable ()
specifier|public
name|boolean
name|getFlippable
parameter_list|()
block|{
return|return
name|flippable
return|;
block|}
DECL|method|doFlip (boolean flip, int x, int max)
specifier|private
name|int
name|doFlip
parameter_list|(
name|boolean
name|flip
parameter_list|,
name|int
name|x
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|flip
condition|)
block|{
return|return
name|max
operator|-
name|x
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|x
return|;
block|}
block|}
DECL|method|getShape (boolean flip, int rotate)
specifier|public
name|boolean
index|[]
index|[]
name|getShape
parameter_list|(
name|boolean
name|flip
parameter_list|,
name|int
name|rotate
parameter_list|)
block|{
name|boolean
index|[]
index|[]
name|result
decl_stmt|;
if|if
condition|(
name|rotate
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|int
name|height
init|=
name|shape
operator|.
name|length
decl_stmt|;
name|int
name|width
init|=
name|shape
index|[
literal|0
index|]
operator|.
name|length
decl_stmt|;
name|result
operator|=
operator|new
name|boolean
index|[
name|height
index|]
index|[]
expr_stmt|;
name|boolean
name|flipX
init|=
name|rotate
operator|==
literal|2
decl_stmt|;
name|boolean
name|flipY
init|=
name|flip
operator|^
operator|(
name|rotate
operator|==
literal|2
operator|)
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|height
condition|;
operator|++
name|y
control|)
block|{
name|result
index|[
name|y
index|]
operator|=
operator|new
name|boolean
index|[
name|width
index|]
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|width
condition|;
operator|++
name|x
control|)
block|{
name|result
index|[
name|y
index|]
index|[
name|x
index|]
operator|=
name|shape
index|[
name|doFlip
argument_list|(
name|flipY
argument_list|,
name|y
argument_list|,
name|height
argument_list|)
index|]
index|[
name|doFlip
argument_list|(
name|flipX
argument_list|,
name|x
argument_list|,
name|width
argument_list|)
index|]
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|int
name|height
init|=
name|shape
index|[
literal|0
index|]
operator|.
name|length
decl_stmt|;
name|int
name|width
init|=
name|shape
operator|.
name|length
decl_stmt|;
name|result
operator|=
operator|new
name|boolean
index|[
name|height
index|]
index|[]
expr_stmt|;
name|boolean
name|flipX
init|=
name|rotate
operator|==
literal|3
decl_stmt|;
name|boolean
name|flipY
init|=
name|flip
operator|^
operator|(
name|rotate
operator|==
literal|1
operator|)
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|height
condition|;
operator|++
name|y
control|)
block|{
name|result
index|[
name|y
index|]
operator|=
operator|new
name|boolean
index|[
name|width
index|]
expr_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|width
condition|;
operator|++
name|x
control|)
block|{
name|result
index|[
name|y
index|]
index|[
name|x
index|]
operator|=
name|shape
index|[
name|doFlip
argument_list|(
name|flipX
argument_list|,
name|x
argument_list|,
name|width
argument_list|)
index|]
index|[
name|doFlip
argument_list|(
name|flipY
argument_list|,
name|y
argument_list|,
name|height
argument_list|)
index|]
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
block|}
comment|/**    * A point in the puzzle board. This represents a placement of a piece into    * a given point on the board.    */
DECL|class|Point
specifier|static
class|class
name|Point
implements|implements
name|ColumnName
block|{
DECL|field|x
name|int
name|x
decl_stmt|;
DECL|field|y
name|int
name|y
decl_stmt|;
DECL|method|Point (int x, int y)
name|Point
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
block|{
name|this
operator|.
name|x
operator|=
name|x
expr_stmt|;
name|this
operator|.
name|y
operator|=
name|y
expr_stmt|;
block|}
block|}
comment|/**    * Convert a solution to the puzzle returned by the model into a string    * that represents the placement of the pieces onto the board.    * @param width the width of the puzzle board    * @param height the height of the puzzle board    * @param solution the list of column names that were selected in the model    * @return a string representation of completed puzzle board    */
DECL|method|stringifySolution (int width, int height, List<List<ColumnName>> solution)
specifier|public
specifier|static
name|String
name|stringifySolution
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|solution
parameter_list|)
block|{
name|String
index|[]
index|[]
name|picture
init|=
operator|new
name|String
index|[
name|height
index|]
index|[
name|width
index|]
decl_stmt|;
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
comment|// for each piece placement...
for|for
control|(
name|List
argument_list|<
name|ColumnName
argument_list|>
name|row
range|:
name|solution
control|)
block|{
comment|// go through to find which piece was placed
name|Piece
name|piece
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ColumnName
name|item
range|:
name|row
control|)
block|{
if|if
condition|(
name|item
operator|instanceof
name|Piece
condition|)
block|{
name|piece
operator|=
operator|(
name|Piece
operator|)
name|item
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|piece
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// for each point where the piece was placed, mark it with the piece name
for|for
control|(
name|ColumnName
name|item
range|:
name|row
control|)
block|{
if|if
condition|(
name|item
operator|instanceof
name|Point
condition|)
block|{
name|Point
name|p
init|=
operator|(
name|Point
operator|)
name|item
decl_stmt|;
name|picture
index|[
name|p
operator|.
name|y
index|]
index|[
name|p
operator|.
name|x
index|]
operator|=
name|piece
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// put the string together
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|picture
operator|.
name|length
condition|;
operator|++
name|y
control|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|picture
index|[
name|y
index|]
operator|.
name|length
condition|;
operator|++
name|x
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|picture
index|[
name|y
index|]
index|[
name|x
index|]
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|enum|SolutionCategory
DECL|enumConstant|UPPER_LEFT
DECL|enumConstant|MID_X
DECL|enumConstant|MID_Y
DECL|enumConstant|CENTER
specifier|public
enum|enum
name|SolutionCategory
block|{
name|UPPER_LEFT
block|,
name|MID_X
block|,
name|MID_Y
block|,
name|CENTER
block|}
comment|/**    * Find whether the solution has the x in the upper left quadrant, the    * x-midline, the y-midline or in the center.    * @param names the solution to check    * @return the catagory of the solution    */
DECL|method|getCategory (List<List<ColumnName>> names)
specifier|public
name|SolutionCategory
name|getCategory
parameter_list|(
name|List
argument_list|<
name|List
argument_list|<
name|ColumnName
argument_list|>
argument_list|>
name|names
parameter_list|)
block|{
name|Piece
name|xPiece
init|=
literal|null
decl_stmt|;
comment|// find the "x" piece
for|for
control|(
name|Piece
name|p
range|:
name|pieces
control|)
block|{
if|if
condition|(
literal|"x"
operator|.
name|equals
argument_list|(
name|p
operator|.
name|name
argument_list|)
condition|)
block|{
name|xPiece
operator|=
name|p
expr_stmt|;
break|break;
block|}
block|}
comment|// find the row containing the "x"
for|for
control|(
name|List
argument_list|<
name|ColumnName
argument_list|>
name|row
range|:
name|names
control|)
block|{
if|if
condition|(
name|row
operator|.
name|contains
argument_list|(
name|xPiece
argument_list|)
condition|)
block|{
comment|// figure out where the "x" is located
name|int
name|low_x
init|=
name|width
decl_stmt|;
name|int
name|high_x
init|=
literal|0
decl_stmt|;
name|int
name|low_y
init|=
name|height
decl_stmt|;
name|int
name|high_y
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ColumnName
name|col
range|:
name|row
control|)
block|{
if|if
condition|(
name|col
operator|instanceof
name|Point
condition|)
block|{
name|int
name|x
init|=
operator|(
operator|(
name|Point
operator|)
name|col
operator|)
operator|.
name|x
decl_stmt|;
name|int
name|y
init|=
operator|(
operator|(
name|Point
operator|)
name|col
operator|)
operator|.
name|y
decl_stmt|;
if|if
condition|(
name|x
operator|<
name|low_x
condition|)
block|{
name|low_x
operator|=
name|x
expr_stmt|;
block|}
if|if
condition|(
name|x
operator|>
name|high_x
condition|)
block|{
name|high_x
operator|=
name|x
expr_stmt|;
block|}
if|if
condition|(
name|y
operator|<
name|low_y
condition|)
block|{
name|low_y
operator|=
name|y
expr_stmt|;
block|}
if|if
condition|(
name|y
operator|>
name|high_y
condition|)
block|{
name|high_y
operator|=
name|y
expr_stmt|;
block|}
block|}
block|}
name|boolean
name|mid_x
init|=
operator|(
name|low_x
operator|+
name|high_x
operator|==
name|width
operator|-
literal|1
operator|)
decl_stmt|;
name|boolean
name|mid_y
init|=
operator|(
name|low_y
operator|+
name|high_y
operator|==
name|height
operator|-
literal|1
operator|)
decl_stmt|;
if|if
condition|(
name|mid_x
operator|&&
name|mid_y
condition|)
block|{
return|return
name|SolutionCategory
operator|.
name|CENTER
return|;
block|}
elseif|else
if|if
condition|(
name|mid_x
condition|)
block|{
return|return
name|SolutionCategory
operator|.
name|MID_X
return|;
block|}
elseif|else
if|if
condition|(
name|mid_y
condition|)
block|{
return|return
name|SolutionCategory
operator|.
name|MID_Y
return|;
block|}
break|break;
block|}
block|}
return|return
name|SolutionCategory
operator|.
name|UPPER_LEFT
return|;
block|}
comment|/**    * A solution printer that just writes the solution to stdout.    */
DECL|class|SolutionPrinter
specifier|private
specifier|static
class|class
name|SolutionPrinter
implements|implements
name|DancingLinks
operator|.
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
block|{
DECL|field|width
name|int
name|width
decl_stmt|;
DECL|field|height
name|int
name|height
decl_stmt|;
DECL|method|SolutionPrinter (int width, int height)
specifier|public
name|SolutionPrinter
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|)
block|{
name|this
operator|.
name|width
operator|=
name|width
expr_stmt|;
name|this
operator|.
name|height
operator|=
name|height
expr_stmt|;
block|}
DECL|method|solution (List<List<ColumnName>> names)
specifier|public
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
name|names
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|stringifySolution
argument_list|(
name|width
argument_list|,
name|height
argument_list|,
name|names
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|width
specifier|protected
name|int
name|width
decl_stmt|;
DECL|field|height
specifier|protected
name|int
name|height
decl_stmt|;
DECL|field|pieces
specifier|protected
name|List
argument_list|<
name|Piece
argument_list|>
name|pieces
init|=
operator|new
name|ArrayList
argument_list|<
name|Piece
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Is the piece fixed under rotation?    */
DECL|field|oneRotation
specifier|protected
specifier|static
specifier|final
name|int
index|[]
name|oneRotation
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|}
decl_stmt|;
comment|/**    * Is the piece identical if rotated 180 degrees?    */
DECL|field|twoRotations
specifier|protected
specifier|static
specifier|final
name|int
index|[]
name|twoRotations
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|}
decl_stmt|;
comment|/**    * Are all 4 rotations unique?    */
DECL|field|fourRotations
specifier|protected
specifier|static
specifier|final
name|int
index|[]
name|fourRotations
init|=
operator|new
name|int
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|}
decl_stmt|;
comment|/**    * Fill in the pieces list.    */
DECL|method|initializePieces ()
specifier|protected
name|void
name|initializePieces
parameter_list|()
block|{
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"x"
argument_list|,
literal|" x /xxx/ x "
argument_list|,
literal|false
argument_list|,
name|oneRotation
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"v"
argument_list|,
literal|"x  /x  /xxx"
argument_list|,
literal|false
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"t"
argument_list|,
literal|"xxx/ x / x "
argument_list|,
literal|false
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"w"
argument_list|,
literal|"  x/ xx/xx "
argument_list|,
literal|false
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"u"
argument_list|,
literal|"x x/xxx"
argument_list|,
literal|false
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"i"
argument_list|,
literal|"xxxxx"
argument_list|,
literal|false
argument_list|,
name|twoRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"f"
argument_list|,
literal|" xx/xx / x "
argument_list|,
literal|true
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"p"
argument_list|,
literal|"xx/xx/x "
argument_list|,
literal|true
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"z"
argument_list|,
literal|"xx / x / xx"
argument_list|,
literal|true
argument_list|,
name|twoRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"n"
argument_list|,
literal|"xx  / xxx"
argument_list|,
literal|true
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"y"
argument_list|,
literal|"  x /xxxx"
argument_list|,
literal|true
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
name|pieces
operator|.
name|add
argument_list|(
operator|new
name|Piece
argument_list|(
literal|"l"
argument_list|,
literal|"   x/xxxx"
argument_list|,
literal|true
argument_list|,
name|fourRotations
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Is the middle of piece on the upper/left side of the board with     * a given offset and size of the piece? This only checks in one    * dimension.    * @param offset the offset of the piece    * @param shapeSize the size of the piece    * @param board the size of the board    * @return is it in the upper/left?    */
DECL|method|isSide (int offset, int shapeSize, int board)
specifier|private
specifier|static
name|boolean
name|isSide
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|shapeSize
parameter_list|,
name|int
name|board
parameter_list|)
block|{
return|return
literal|2
operator|*
name|offset
operator|+
name|shapeSize
operator|<=
name|board
return|;
block|}
comment|/**    * For a given piece, generate all of the potential placements and add them     * as rows to the model.    * @param dancer the problem model    * @param piece the piece we are trying to place    * @param width the width of the board    * @param height the height of the board    * @param flip is the piece flipped over?    * @param row a workspace the length of the each row in the table    * @param upperLeft is the piece constrained to the upper left of the board?    *        this is used on a single piece to eliminate most of the trivial    *        roations of the solution.    */
DECL|method|generateRows (DancingLinks dancer, Piece piece, int width, int height, boolean flip, boolean[] row, boolean upperLeft)
specifier|private
specifier|static
name|void
name|generateRows
parameter_list|(
name|DancingLinks
name|dancer
parameter_list|,
name|Piece
name|piece
parameter_list|,
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|,
name|boolean
name|flip
parameter_list|,
name|boolean
index|[]
name|row
parameter_list|,
name|boolean
name|upperLeft
parameter_list|)
block|{
comment|// for each rotation
name|int
index|[]
name|rotations
init|=
name|piece
operator|.
name|getRotations
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|rotIndex
init|=
literal|0
init|;
name|rotIndex
operator|<
name|rotations
operator|.
name|length
condition|;
operator|++
name|rotIndex
control|)
block|{
comment|// get the shape
name|boolean
index|[]
index|[]
name|shape
init|=
name|piece
operator|.
name|getShape
argument_list|(
name|flip
argument_list|,
name|rotations
index|[
name|rotIndex
index|]
argument_list|)
decl_stmt|;
comment|// find all of the valid offsets
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|width
condition|;
operator|++
name|x
control|)
block|{
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|height
condition|;
operator|++
name|y
control|)
block|{
if|if
condition|(
name|y
operator|+
name|shape
operator|.
name|length
operator|<=
name|height
operator|&&
name|x
operator|+
name|shape
index|[
literal|0
index|]
operator|.
name|length
operator|<=
name|width
operator|&&
operator|(
operator|!
name|upperLeft
operator|||
operator|(
name|isSide
argument_list|(
name|x
argument_list|,
name|shape
index|[
literal|0
index|]
operator|.
name|length
argument_list|,
name|width
argument_list|)
operator|&&
name|isSide
argument_list|(
name|y
argument_list|,
name|shape
operator|.
name|length
argument_list|,
name|height
argument_list|)
operator|)
operator|)
condition|)
block|{
comment|// clear the columns related to the points on the board
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|width
operator|*
name|height
condition|;
operator|++
name|idx
control|)
block|{
name|row
index|[
name|idx
index|]
operator|=
literal|false
expr_stmt|;
block|}
comment|// mark the shape
for|for
control|(
name|int
name|subY
init|=
literal|0
init|;
name|subY
operator|<
name|shape
operator|.
name|length
condition|;
operator|++
name|subY
control|)
block|{
for|for
control|(
name|int
name|subX
init|=
literal|0
init|;
name|subX
operator|<
name|shape
index|[
literal|0
index|]
operator|.
name|length
condition|;
operator|++
name|subX
control|)
block|{
name|row
index|[
operator|(
name|y
operator|+
name|subY
operator|)
operator|*
name|width
operator|+
name|x
operator|+
name|subX
index|]
operator|=
name|shape
index|[
name|subY
index|]
index|[
name|subX
index|]
expr_stmt|;
block|}
block|}
name|dancer
operator|.
name|addRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|field|dancer
specifier|private
name|DancingLinks
argument_list|<
name|ColumnName
argument_list|>
name|dancer
init|=
operator|new
name|DancingLinks
argument_list|<
name|ColumnName
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|printer
specifier|private
name|DancingLinks
operator|.
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
name|printer
decl_stmt|;
block|{
name|initializePieces
parameter_list|()
constructor_decl|;
block|}
comment|/**    * Create the model for a given pentomino set of pieces and board size.    * @param width the width of the board in squares    * @param height the height of the board in squares    */
DECL|method|Pentomino (int width, int height)
specifier|public
name|Pentomino
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|)
block|{
name|initialize
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create the object without initialization.    */
DECL|method|Pentomino ()
specifier|public
name|Pentomino
parameter_list|()
block|{   }
DECL|method|initialize (int width, int height)
name|void
name|initialize
parameter_list|(
name|int
name|width
parameter_list|,
name|int
name|height
parameter_list|)
block|{
name|this
operator|.
name|width
operator|=
name|width
expr_stmt|;
name|this
operator|.
name|height
operator|=
name|height
expr_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|height
condition|;
operator|++
name|y
control|)
block|{
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|width
condition|;
operator|++
name|x
control|)
block|{
name|dancer
operator|.
name|addColumn
argument_list|(
operator|new
name|Point
argument_list|(
name|x
argument_list|,
name|y
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|pieceBase
init|=
name|dancer
operator|.
name|getNumberColumns
argument_list|()
decl_stmt|;
for|for
control|(
name|Piece
name|p
range|:
name|pieces
control|)
block|{
name|dancer
operator|.
name|addColumn
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|boolean
index|[]
name|row
init|=
operator|new
name|boolean
index|[
name|dancer
operator|.
name|getNumberColumns
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|pieces
operator|.
name|size
argument_list|()
condition|;
operator|++
name|idx
control|)
block|{
name|Piece
name|piece
init|=
name|pieces
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|row
index|[
name|idx
operator|+
name|pieceBase
index|]
operator|=
literal|true
expr_stmt|;
name|generateRows
argument_list|(
name|dancer
argument_list|,
name|piece
argument_list|,
name|width
argument_list|,
name|height
argument_list|,
literal|false
argument_list|,
name|row
argument_list|,
name|idx
operator|==
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|piece
operator|.
name|getFlippable
argument_list|()
condition|)
block|{
name|generateRows
argument_list|(
name|dancer
argument_list|,
name|piece
argument_list|,
name|width
argument_list|,
name|height
argument_list|,
literal|true
argument_list|,
name|row
argument_list|,
name|idx
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|row
index|[
name|idx
operator|+
name|pieceBase
index|]
operator|=
literal|false
expr_stmt|;
block|}
name|printer
operator|=
operator|new
name|SolutionPrinter
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate a list of prefixes to a given depth    * @param depth the length of each prefix    * @return a list of arrays of ints, which are potential prefixes    */
DECL|method|getSplits (int depth)
specifier|public
name|List
argument_list|<
name|int
index|[]
argument_list|>
name|getSplits
parameter_list|(
name|int
name|depth
parameter_list|)
block|{
return|return
name|dancer
operator|.
name|split
argument_list|(
name|depth
argument_list|)
return|;
block|}
comment|/**    * Find all of the solutions that start with the given prefix. The printer    * is given each solution as it is found.    * @param split a list of row indexes that should be chosen for each row    *        in order    * @return the number of solutions found    */
DECL|method|solve (int[] split)
specifier|public
name|int
name|solve
parameter_list|(
name|int
index|[]
name|split
parameter_list|)
block|{
return|return
name|dancer
operator|.
name|solve
argument_list|(
name|split
argument_list|,
name|printer
argument_list|)
return|;
block|}
comment|/**    * Find all of the solutions to the puzzle.    * @return the number of solutions found    */
DECL|method|solve ()
specifier|public
name|int
name|solve
parameter_list|()
block|{
return|return
name|dancer
operator|.
name|solve
argument_list|(
name|printer
argument_list|)
return|;
block|}
comment|/**    * Set the printer for the puzzle.    * @param printer A call-back object that is given each solution as it is     * found.    */
DECL|method|setPrinter (DancingLinks.SolutionAcceptor<ColumnName> printer)
specifier|public
name|void
name|setPrinter
parameter_list|(
name|DancingLinks
operator|.
name|SolutionAcceptor
argument_list|<
name|ColumnName
argument_list|>
name|printer
parameter_list|)
block|{
name|this
operator|.
name|printer
operator|=
name|printer
expr_stmt|;
block|}
comment|/**    * Solve the 6x10 pentomino puzzle.    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|int
name|width
init|=
literal|6
decl_stmt|;
name|int
name|height
init|=
literal|10
decl_stmt|;
name|Pentomino
name|model
init|=
operator|new
name|Pentomino
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
decl_stmt|;
name|List
name|splits
init|=
name|model
operator|.
name|getSplits
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|splitItr
init|=
name|splits
operator|.
name|iterator
argument_list|()
init|;
name|splitItr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|int
index|[]
name|choices
init|=
operator|(
name|int
index|[]
operator|)
name|splitItr
operator|.
name|next
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"split:"
argument_list|)
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
name|choices
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|choices
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|model
operator|.
name|solve
argument_list|(
name|choices
argument_list|)
operator|+
literal|" solutions found."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

