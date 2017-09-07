begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|fs
operator|.
name|StreamCapabilities
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
name|fs
operator|.
name|Syncable
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
comment|/**  * Support the Syncable interface on top of a DataOutputStream.  * This allows passing the sync/hflush/hsync calls through to the  * wrapped stream passed in to the constructor. This is required  * for HBase when wrapping a PageBlobOutputStream used as a write-ahead log.  */
end_comment

begin_class
DECL|class|SyncableDataOutputStream
specifier|public
class|class
name|SyncableDataOutputStream
extends|extends
name|DataOutputStream
implements|implements
name|Syncable
implements|,
name|StreamCapabilities
block|{
DECL|method|SyncableDataOutputStream (OutputStream out)
specifier|public
name|SyncableDataOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get a reference to the wrapped output stream.    *    * @return the underlying output stream    */
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|}
argument_list|)
DECL|method|getOutStream ()
specifier|public
name|OutputStream
name|getOutStream
parameter_list|()
block|{
return|return
name|out
return|;
block|}
annotation|@
name|Override
DECL|method|hasCapability (String capability)
specifier|public
name|boolean
name|hasCapability
parameter_list|(
name|String
name|capability
parameter_list|)
block|{
if|if
condition|(
name|out
operator|instanceof
name|StreamCapabilities
condition|)
block|{
return|return
operator|(
operator|(
name|StreamCapabilities
operator|)
name|out
operator|)
operator|.
name|hasCapability
argument_list|(
name|capability
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hflush ()
specifier|public
name|void
name|hflush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|instanceof
name|Syncable
condition|)
block|{
operator|(
operator|(
name|Syncable
operator|)
name|out
operator|)
operator|.
name|hflush
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|hsync ()
specifier|public
name|void
name|hsync
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|out
operator|instanceof
name|Syncable
condition|)
block|{
operator|(
operator|(
name|Syncable
operator|)
name|out
operator|)
operator|.
name|hsync
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

