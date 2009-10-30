begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
comment|/** This interface for flush/sync operation. */
end_comment

begin_interface
DECL|interface|Syncable
specifier|public
interface|interface
name|Syncable
block|{
comment|/**    * @deprecated As of HADOOP 0.21.0, replaced by hflush    * @see #hflush()    */
DECL|method|sync ()
annotation|@
name|Deprecated
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Flush out the data in client's user buffer. After the return of    * this call, new readers will see the data.    * @throws IOException if any error occurs    */
DECL|method|hflush ()
specifier|public
name|void
name|hflush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** Similar to posix fsync, flush out the data in client's user buffer     * all the way to the disk device (but the disk may have it in its cache).    * @throws IOException if error occurs    */
DECL|method|hsync ()
specifier|public
name|void
name|hsync
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

