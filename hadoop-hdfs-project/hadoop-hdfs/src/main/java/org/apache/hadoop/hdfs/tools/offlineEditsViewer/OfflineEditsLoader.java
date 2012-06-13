begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
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
name|offlineEditsViewer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|InterfaceStability
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
name|tools
operator|.
name|offlineEditsViewer
operator|.
name|OfflineEditsViewer
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
name|protocol
operator|.
name|HdfsConstants
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
name|server
operator|.
name|namenode
operator|.
name|EditLogFileInputStream
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
name|server
operator|.
name|namenode
operator|.
name|EditLogInputStream
import|;
end_import

begin_comment
comment|/**  * OfflineEditsLoader walks an EditsVisitor over an EditLogInputStream  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|OfflineEditsLoader
interface|interface
name|OfflineEditsLoader
block|{
DECL|method|loadEdits ()
specifier|abstract
specifier|public
name|void
name|loadEdits
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|class|OfflineEditsLoaderFactory
specifier|static
class|class
name|OfflineEditsLoaderFactory
block|{
DECL|method|createLoader (OfflineEditsVisitor visitor, String inputFileName, boolean xmlInput, OfflineEditsViewer.Flags flags)
specifier|static
name|OfflineEditsLoader
name|createLoader
parameter_list|(
name|OfflineEditsVisitor
name|visitor
parameter_list|,
name|String
name|inputFileName
parameter_list|,
name|boolean
name|xmlInput
parameter_list|,
name|OfflineEditsViewer
operator|.
name|Flags
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|xmlInput
condition|)
block|{
return|return
operator|new
name|OfflineEditsXmlLoader
argument_list|(
name|visitor
argument_list|,
operator|new
name|File
argument_list|(
name|inputFileName
argument_list|)
argument_list|,
name|flags
argument_list|)
return|;
block|}
else|else
block|{
name|File
name|file
init|=
literal|null
decl_stmt|;
name|EditLogInputStream
name|elis
init|=
literal|null
decl_stmt|;
name|OfflineEditsLoader
name|loader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|inputFileName
argument_list|)
expr_stmt|;
name|elis
operator|=
operator|new
name|EditLogFileInputStream
argument_list|(
name|file
argument_list|,
name|HdfsConstants
operator|.
name|INVALID_TXID
argument_list|,
name|HdfsConstants
operator|.
name|INVALID_TXID
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|loader
operator|=
operator|new
name|OfflineEditsBinaryLoader
argument_list|(
name|visitor
argument_list|,
name|elis
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|(
name|loader
operator|==
literal|null
operator|)
operator|&&
operator|(
name|elis
operator|!=
literal|null
operator|)
condition|)
block|{
name|elis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|loader
return|;
block|}
block|}
block|}
block|}
end_interface

end_unit

