begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.common
package|package
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
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|io
operator|.
name|Writable
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
name|io
operator|.
name|WritableUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_comment
comment|/**  * Common class for storage information.  *   * TODO namespaceID should be long and computed as hash(address + port)  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StorageInfo
specifier|public
class|class
name|StorageInfo
implements|implements
name|Writable
block|{
DECL|field|layoutVersion
specifier|public
name|int
name|layoutVersion
decl_stmt|;
comment|// layout version of the storage data
DECL|field|namespaceID
specifier|public
name|int
name|namespaceID
decl_stmt|;
comment|// id of the file system
DECL|field|clusterID
specifier|public
name|String
name|clusterID
decl_stmt|;
comment|// id of the cluster
DECL|field|cTime
specifier|public
name|long
name|cTime
decl_stmt|;
comment|// creation time of the file system state
DECL|method|StorageInfo ()
specifier|public
name|StorageInfo
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|StorageInfo (int layoutV, int nsID, String cid, long cT)
specifier|public
name|StorageInfo
parameter_list|(
name|int
name|layoutV
parameter_list|,
name|int
name|nsID
parameter_list|,
name|String
name|cid
parameter_list|,
name|long
name|cT
parameter_list|)
block|{
name|layoutVersion
operator|=
name|layoutV
expr_stmt|;
name|clusterID
operator|=
name|cid
expr_stmt|;
name|namespaceID
operator|=
name|nsID
expr_stmt|;
name|cTime
operator|=
name|cT
expr_stmt|;
block|}
DECL|method|StorageInfo (StorageInfo from)
specifier|public
name|StorageInfo
parameter_list|(
name|StorageInfo
name|from
parameter_list|)
block|{
name|setStorageInfo
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
comment|/**    * Layout version of the storage data.    */
DECL|method|getLayoutVersion ()
specifier|public
name|int
name|getLayoutVersion
parameter_list|()
block|{
return|return
name|layoutVersion
return|;
block|}
comment|/**    * Namespace id of the file system.<p>    * Assigned to the file system at formatting and never changes after that.    * Shared by all file system components.    */
DECL|method|getNamespaceID ()
specifier|public
name|int
name|getNamespaceID
parameter_list|()
block|{
return|return
name|namespaceID
return|;
block|}
comment|/**    * cluster id of the file system.<p>    */
DECL|method|getClusterID ()
specifier|public
name|String
name|getClusterID
parameter_list|()
block|{
return|return
name|clusterID
return|;
block|}
comment|/**    * Creation time of the file system state.<p>    * Modified during upgrades.    */
DECL|method|getCTime ()
specifier|public
name|long
name|getCTime
parameter_list|()
block|{
return|return
name|cTime
return|;
block|}
DECL|method|setStorageInfo (StorageInfo from)
specifier|public
name|void
name|setStorageInfo
parameter_list|(
name|StorageInfo
name|from
parameter_list|)
block|{
name|layoutVersion
operator|=
name|from
operator|.
name|layoutVersion
expr_stmt|;
name|clusterID
operator|=
name|from
operator|.
name|clusterID
expr_stmt|;
name|namespaceID
operator|=
name|from
operator|.
name|namespaceID
expr_stmt|;
name|cTime
operator|=
name|from
operator|.
name|cTime
expr_stmt|;
block|}
comment|/////////////////////////////////////////////////
comment|// Writable
comment|/////////////////////////////////////////////////
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|getLayoutVersion
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|getNamespaceID
argument_list|()
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|clusterID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|getCTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|layoutVersion
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|namespaceID
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|clusterID
operator|=
name|WritableUtils
operator|.
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|cTime
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"lv="
argument_list|)
operator|.
name|append
argument_list|(
name|layoutVersion
argument_list|)
operator|.
name|append
argument_list|(
literal|";cid="
argument_list|)
operator|.
name|append
argument_list|(
name|clusterID
argument_list|)
operator|.
name|append
argument_list|(
literal|";nsid="
argument_list|)
operator|.
name|append
argument_list|(
name|namespaceID
argument_list|)
operator|.
name|append
argument_list|(
literal|";c="
argument_list|)
operator|.
name|append
argument_list|(
name|cTime
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|toColonSeparatedString ()
specifier|public
name|String
name|toColonSeparatedString
parameter_list|()
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|":"
argument_list|)
operator|.
name|join
argument_list|(
name|layoutVersion
argument_list|,
name|namespaceID
argument_list|,
name|cTime
argument_list|,
name|clusterID
argument_list|)
return|;
block|}
block|}
end_class

end_unit

