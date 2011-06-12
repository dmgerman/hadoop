begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineImageViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * IndentedImageVisitor walks over an FSImage and displays its structure   * using indenting to organize sections within the image file.  */
end_comment

begin_class
DECL|class|IndentedImageVisitor
class|class
name|IndentedImageVisitor
extends|extends
name|TextWriterImageVisitor
block|{
DECL|method|IndentedImageVisitor (String filename)
specifier|public
name|IndentedImageVisitor
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
DECL|method|IndentedImageVisitor (String filename, boolean printToScreen)
specifier|public
name|IndentedImageVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|boolean
name|printToScreen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|filename
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
block|}
DECL|field|dc
specifier|final
specifier|private
name|DepthCounter
name|dc
init|=
operator|new
name|DepthCounter
argument_list|()
decl_stmt|;
comment|// to track leading spacing
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"*** Image processing finished abnormally.  Ending ***"
argument_list|)
expr_stmt|;
name|super
operator|.
name|finishAbnormally
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{
name|dc
operator|.
name|decLevel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visit (ImageElement element, String value)
name|void
name|visit
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|printIndents
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|element
operator|+
literal|" = "
operator|+
name|value
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|)
throws|throws
name|IOException
block|{
name|printIndents
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|element
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|dc
operator|.
name|incLevel
argument_list|()
expr_stmt|;
block|}
comment|// Print element, along with associated key/value pair, in brackets
annotation|@
name|Override
DECL|method|visitEnclosingElement (ImageElement element, ImageElement key, String value)
name|void
name|visitEnclosingElement
parameter_list|(
name|ImageElement
name|element
parameter_list|,
name|ImageElement
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|printIndents
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|element
operator|+
literal|" ["
operator|+
name|key
operator|+
literal|" = "
operator|+
name|value
operator|+
literal|"]\n"
argument_list|)
expr_stmt|;
name|dc
operator|.
name|incLevel
argument_list|()
expr_stmt|;
block|}
comment|/**   * Print an appropriate number of spaces for the current level.   * FsImages can potentially be millions of lines long, so caching can   * significantly speed up output.   */
DECL|field|indents
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|indents
init|=
block|{
literal|""
block|,
literal|"  "
block|,
literal|"    "
block|,
literal|"      "
block|,
literal|"        "
block|,
literal|"          "
block|,
literal|"            "
block|}
decl_stmt|;
DECL|method|printIndents ()
specifier|private
name|void
name|printIndents
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|write
argument_list|(
name|indents
index|[
name|dc
operator|.
name|getLevel
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// There's no reason in an fsimage would need a deeper indent
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|dc
operator|.
name|getLevel
argument_list|()
condition|;
name|i
operator|++
control|)
name|write
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

