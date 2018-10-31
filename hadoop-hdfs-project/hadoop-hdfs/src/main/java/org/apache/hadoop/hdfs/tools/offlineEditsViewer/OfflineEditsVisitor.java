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
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOp
import|;
end_import

begin_comment
comment|/**  * An implementation of OfflineEditsVisitor can traverse the structure of an  * Hadoop edits log and respond to each of the structures within the file.  */
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
DECL|interface|OfflineEditsVisitor
specifier|abstract
specifier|public
interface|interface
name|OfflineEditsVisitor
block|{
comment|/**    * Begin visiting the edits log structure.  Opportunity to perform    * any initialization necessary for the implementing visitor.    *     * @param version     Edit log version    */
DECL|method|start (int version)
specifier|abstract
name|void
name|start
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Finish visiting the edits log structure.  Opportunity to perform any    * clean up necessary for the implementing visitor.    *     * @param error        If the visitor was closed because of an     *                     unrecoverable error in the input stream, this     *                     is the exception.    */
DECL|method|close (Throwable error)
specifier|abstract
name|void
name|close
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Begin visiting an element that encloses another element, such as    * the beginning of the list of blocks that comprise a file.    *    * @param op Token being visited    */
DECL|method|visitOp (FSEditLogOp op)
specifier|abstract
name|void
name|visitOp
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

