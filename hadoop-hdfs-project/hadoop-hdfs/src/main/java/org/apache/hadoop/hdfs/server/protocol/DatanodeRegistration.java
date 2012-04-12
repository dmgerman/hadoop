begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
package|;
end_package

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
name|protocol
operator|.
name|DatanodeID
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|ExportedBlockKeys
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
name|common
operator|.
name|Storage
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
name|common
operator|.
name|StorageInfo
import|;
end_import

begin_comment
comment|/**   * DatanodeRegistration class contains all information the name-node needs  * to identify and verify a data-node when it contacts the name-node.  * This information is sent by data-node with each communication request.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DatanodeRegistration
specifier|public
class|class
name|DatanodeRegistration
extends|extends
name|DatanodeID
implements|implements
name|NodeRegistration
block|{
DECL|field|storageInfo
specifier|private
name|StorageInfo
name|storageInfo
decl_stmt|;
DECL|field|exportedKeys
specifier|private
name|ExportedBlockKeys
name|exportedKeys
decl_stmt|;
DECL|field|softwareVersion
specifier|private
name|String
name|softwareVersion
decl_stmt|;
DECL|method|DatanodeRegistration (DatanodeID dn, StorageInfo info, ExportedBlockKeys keys, String softwareVersion)
specifier|public
name|DatanodeRegistration
parameter_list|(
name|DatanodeID
name|dn
parameter_list|,
name|StorageInfo
name|info
parameter_list|,
name|ExportedBlockKeys
name|keys
parameter_list|,
name|String
name|softwareVersion
parameter_list|)
block|{
name|super
argument_list|(
name|dn
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|exportedKeys
operator|=
name|keys
expr_stmt|;
name|this
operator|.
name|softwareVersion
operator|=
name|softwareVersion
expr_stmt|;
block|}
DECL|method|DatanodeRegistration (String ipAddr, int xferPort)
specifier|public
name|DatanodeRegistration
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|int
name|xferPort
parameter_list|)
block|{
name|this
argument_list|(
name|ipAddr
argument_list|,
name|xferPort
argument_list|,
operator|new
name|StorageInfo
argument_list|()
argument_list|,
operator|new
name|ExportedBlockKeys
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DatanodeRegistration (String ipAddr, int xferPort, StorageInfo info, ExportedBlockKeys keys)
specifier|public
name|DatanodeRegistration
parameter_list|(
name|String
name|ipAddr
parameter_list|,
name|int
name|xferPort
parameter_list|,
name|StorageInfo
name|info
parameter_list|,
name|ExportedBlockKeys
name|keys
parameter_list|)
block|{
name|super
argument_list|(
name|ipAddr
argument_list|,
name|xferPort
argument_list|)
expr_stmt|;
name|this
operator|.
name|storageInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|exportedKeys
operator|=
name|keys
expr_stmt|;
block|}
DECL|method|setStorageInfo (StorageInfo storage)
specifier|public
name|void
name|setStorageInfo
parameter_list|(
name|StorageInfo
name|storage
parameter_list|)
block|{
name|this
operator|.
name|storageInfo
operator|=
operator|new
name|StorageInfo
argument_list|(
name|storage
argument_list|)
expr_stmt|;
block|}
DECL|method|getStorageInfo ()
specifier|public
name|StorageInfo
name|getStorageInfo
parameter_list|()
block|{
return|return
name|storageInfo
return|;
block|}
DECL|method|setExportedKeys (ExportedBlockKeys keys)
specifier|public
name|void
name|setExportedKeys
parameter_list|(
name|ExportedBlockKeys
name|keys
parameter_list|)
block|{
name|this
operator|.
name|exportedKeys
operator|=
name|keys
expr_stmt|;
block|}
DECL|method|getExportedKeys ()
specifier|public
name|ExportedBlockKeys
name|getExportedKeys
parameter_list|()
block|{
return|return
name|exportedKeys
return|;
block|}
DECL|method|setSoftwareVersion (String softwareVersion)
specifier|public
name|void
name|setSoftwareVersion
parameter_list|(
name|String
name|softwareVersion
parameter_list|)
block|{
name|this
operator|.
name|softwareVersion
operator|=
name|softwareVersion
expr_stmt|;
block|}
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
parameter_list|()
block|{
return|return
name|softwareVersion
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getVersion ()
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|storageInfo
operator|.
name|getLayoutVersion
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getRegistrationID ()
specifier|public
name|String
name|getRegistrationID
parameter_list|()
block|{
return|return
name|Storage
operator|.
name|getRegistrationID
argument_list|(
name|storageInfo
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// NodeRegistration
DECL|method|getAddress ()
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
return|return
name|getXferAddr
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"("
operator|+
name|getIpAddr
argument_list|()
operator|+
literal|", storageID="
operator|+
name|storageID
operator|+
literal|", infoPort="
operator|+
name|infoPort
operator|+
literal|", ipcPort="
operator|+
name|ipcPort
operator|+
literal|", storageInfo="
operator|+
name|storageInfo
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object to)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|to
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|to
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

