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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|hdfs
operator|.
name|util
operator|.
name|XMLUtils
import|;
end_import

begin_comment
comment|/**  * An XmlImageVisitor walks over an fsimage structure and writes out  * an equivalent XML document that contains the fsimage's components.  */
end_comment

begin_class
DECL|class|XmlImageVisitor
specifier|public
class|class
name|XmlImageVisitor
extends|extends
name|TextWriterImageVisitor
block|{
DECL|field|tagQ
specifier|final
specifier|private
name|Deque
argument_list|<
name|ImageElement
argument_list|>
name|tagQ
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|XmlImageVisitor (String filename)
specifier|public
name|XmlImageVisitor
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
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|XmlImageVisitor (String filename, boolean printToScreen)
specifier|public
name|XmlImageVisitor
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
name|write
argument_list|(
literal|"\n<!-- Error processing image file.  Exiting -->\n"
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
if|if
condition|(
name|tagQ
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Tried to exit non-existent enclosing element "
operator|+
literal|"in FSImage file"
argument_list|)
throw|;
block|}
name|ImageElement
name|element
init|=
name|tagQ
operator|.
name|pop
argument_list|()
decl_stmt|;
name|write
argument_list|(
literal|"</"
operator|+
name|element
operator|.
name|toString
argument_list|()
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
literal|"<?xml version=\"1.0\" ?>\n"
argument_list|)
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
name|writeTag
argument_list|(
name|element
operator|.
name|toString
argument_list|()
argument_list|,
name|value
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
name|write
argument_list|(
literal|'<'
operator|+
name|element
operator|.
name|toString
argument_list|()
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
name|tagQ
operator|.
name|push
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
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
name|write
argument_list|(
literal|'<'
operator|+
name|element
operator|.
name|toString
argument_list|()
operator|+
literal|' '
operator|+
name|key
operator|+
literal|"=\""
operator|+
name|value
operator|+
literal|"\">\n"
argument_list|)
expr_stmt|;
name|tagQ
operator|.
name|push
argument_list|(
name|element
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTag (String tag, String value)
specifier|private
name|void
name|writeTag
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|write
argument_list|(
literal|'<'
operator|+
name|tag
operator|+
literal|'>'
operator|+
name|XMLUtils
operator|.
name|mangleXmlString
argument_list|(
name|value
argument_list|,
literal|true
argument_list|)
operator|+
literal|"</"
operator|+
name|tag
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

