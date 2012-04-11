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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|MetaRecoveryContext
import|;
end_import

begin_comment
comment|/************************************  * Some handy internal HDFS constants  *  ************************************/
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HdfsServerConstants
specifier|public
specifier|final
class|class
name|HdfsServerConstants
block|{
comment|/* Hidden constructor */
DECL|method|HdfsServerConstants ()
specifier|private
name|HdfsServerConstants
parameter_list|()
block|{ }
comment|/**    * Type of the node    */
DECL|enum|NodeType
specifier|static
specifier|public
enum|enum
name|NodeType
block|{
DECL|enumConstant|NAME_NODE
name|NAME_NODE
block|,
DECL|enumConstant|DATA_NODE
name|DATA_NODE
block|;   }
comment|/** Startup options */
DECL|enum|StartupOption
specifier|static
specifier|public
enum|enum
name|StartupOption
block|{
DECL|enumConstant|FORMAT
name|FORMAT
argument_list|(
literal|"-format"
argument_list|)
block|,
DECL|enumConstant|CLUSTERID
name|CLUSTERID
argument_list|(
literal|"-clusterid"
argument_list|)
block|,
DECL|enumConstant|GENCLUSTERID
name|GENCLUSTERID
argument_list|(
literal|"-genclusterid"
argument_list|)
block|,
DECL|enumConstant|REGULAR
name|REGULAR
argument_list|(
literal|"-regular"
argument_list|)
block|,
DECL|enumConstant|BACKUP
name|BACKUP
argument_list|(
literal|"-backup"
argument_list|)
block|,
DECL|enumConstant|CHECKPOINT
name|CHECKPOINT
argument_list|(
literal|"-checkpoint"
argument_list|)
block|,
DECL|enumConstant|UPGRADE
name|UPGRADE
argument_list|(
literal|"-upgrade"
argument_list|)
block|,
DECL|enumConstant|ROLLBACK
name|ROLLBACK
argument_list|(
literal|"-rollback"
argument_list|)
block|,
DECL|enumConstant|FINALIZE
name|FINALIZE
argument_list|(
literal|"-finalize"
argument_list|)
block|,
DECL|enumConstant|IMPORT
name|IMPORT
argument_list|(
literal|"-importCheckpoint"
argument_list|)
block|,
DECL|enumConstant|BOOTSTRAPSTANDBY
name|BOOTSTRAPSTANDBY
argument_list|(
literal|"-bootstrapStandby"
argument_list|)
block|,
DECL|enumConstant|INITIALIZESHAREDEDITS
name|INITIALIZESHAREDEDITS
argument_list|(
literal|"-initializeSharedEdits"
argument_list|)
block|,
DECL|enumConstant|RECOVER
name|RECOVER
argument_list|(
literal|"-recover"
argument_list|)
block|,
DECL|enumConstant|FORCE
name|FORCE
argument_list|(
literal|"-force"
argument_list|)
block|,
DECL|enumConstant|NONINTERACTIVE
name|NONINTERACTIVE
argument_list|(
literal|"-nonInteractive"
argument_list|)
block|;
DECL|field|name
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
comment|// Used only with format and upgrade options
DECL|field|clusterId
specifier|private
name|String
name|clusterId
init|=
literal|null
decl_stmt|;
comment|// Used only with format option
DECL|field|isForceFormat
specifier|private
name|boolean
name|isForceFormat
init|=
literal|false
decl_stmt|;
DECL|field|isInteractiveFormat
specifier|private
name|boolean
name|isInteractiveFormat
init|=
literal|true
decl_stmt|;
comment|// Used only with recovery option
DECL|field|force
specifier|private
name|int
name|force
init|=
literal|0
decl_stmt|;
DECL|method|StartupOption (String arg)
specifier|private
name|StartupOption
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|arg
expr_stmt|;
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
DECL|method|toNodeRole ()
specifier|public
name|NamenodeRole
name|toNodeRole
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|BACKUP
case|:
return|return
name|NamenodeRole
operator|.
name|BACKUP
return|;
case|case
name|CHECKPOINT
case|:
return|return
name|NamenodeRole
operator|.
name|CHECKPOINT
return|;
default|default:
return|return
name|NamenodeRole
operator|.
name|NAMENODE
return|;
block|}
block|}
DECL|method|setClusterId (String cid)
specifier|public
name|void
name|setClusterId
parameter_list|(
name|String
name|cid
parameter_list|)
block|{
name|clusterId
operator|=
name|cid
expr_stmt|;
block|}
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
DECL|method|createRecoveryContext ()
specifier|public
name|MetaRecoveryContext
name|createRecoveryContext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|RECOVER
operator|.
name|name
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|MetaRecoveryContext
argument_list|(
name|force
argument_list|)
return|;
block|}
DECL|method|setForce (int force)
specifier|public
name|void
name|setForce
parameter_list|(
name|int
name|force
parameter_list|)
block|{
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
block|}
DECL|method|getForce ()
specifier|public
name|int
name|getForce
parameter_list|()
block|{
return|return
name|this
operator|.
name|force
return|;
block|}
DECL|method|getForceFormat ()
specifier|public
name|boolean
name|getForceFormat
parameter_list|()
block|{
return|return
name|isForceFormat
return|;
block|}
DECL|method|setForceFormat (boolean force)
specifier|public
name|void
name|setForceFormat
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|isForceFormat
operator|=
name|force
expr_stmt|;
block|}
DECL|method|getInteractiveFormat ()
specifier|public
name|boolean
name|getInteractiveFormat
parameter_list|()
block|{
return|return
name|isInteractiveFormat
return|;
block|}
DECL|method|setInteractiveFormat (boolean interactive)
specifier|public
name|void
name|setInteractiveFormat
parameter_list|(
name|boolean
name|interactive
parameter_list|)
block|{
name|isInteractiveFormat
operator|=
name|interactive
expr_stmt|;
block|}
block|}
comment|// Timeouts for communicating with DataNode for streaming writes/reads
DECL|field|READ_TIMEOUT
specifier|public
specifier|static
name|int
name|READ_TIMEOUT
init|=
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|READ_TIMEOUT_EXTENSION
specifier|public
specifier|static
name|int
name|READ_TIMEOUT_EXTENSION
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
DECL|field|WRITE_TIMEOUT
specifier|public
specifier|static
name|int
name|WRITE_TIMEOUT
init|=
literal|8
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
DECL|field|WRITE_TIMEOUT_EXTENSION
specifier|public
specifier|static
name|int
name|WRITE_TIMEOUT_EXTENSION
init|=
literal|5
operator|*
literal|1000
decl_stmt|;
comment|//for write pipeline
comment|/**    * Defines the NameNode role.    */
DECL|enum|NamenodeRole
specifier|static
specifier|public
enum|enum
name|NamenodeRole
block|{
DECL|enumConstant|NAMENODE
name|NAMENODE
argument_list|(
literal|"NameNode"
argument_list|)
block|,
DECL|enumConstant|BACKUP
name|BACKUP
argument_list|(
literal|"Backup Node"
argument_list|)
block|,
DECL|enumConstant|CHECKPOINT
name|CHECKPOINT
argument_list|(
literal|"Checkpoint Node"
argument_list|)
block|;
DECL|field|description
specifier|private
name|String
name|description
init|=
literal|null
decl_stmt|;
DECL|method|NamenodeRole (String arg)
specifier|private
name|NamenodeRole
parameter_list|(
name|String
name|arg
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|arg
expr_stmt|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
comment|/**    * Block replica states, which it can go through while being constructed.    */
DECL|enum|ReplicaState
specifier|static
specifier|public
enum|enum
name|ReplicaState
block|{
comment|/** Replica is finalized. The state when replica is not modified. */
DECL|enumConstant|FINALIZED
name|FINALIZED
argument_list|(
literal|0
argument_list|)
block|,
comment|/** Replica is being written to. */
DECL|enumConstant|RBW
name|RBW
argument_list|(
literal|1
argument_list|)
block|,
comment|/** Replica is waiting to be recovered. */
DECL|enumConstant|RWR
name|RWR
argument_list|(
literal|2
argument_list|)
block|,
comment|/** Replica is under recovery. */
DECL|enumConstant|RUR
name|RUR
argument_list|(
literal|3
argument_list|)
block|,
comment|/** Temporary replica: created for replication and relocation only. */
DECL|enumConstant|TEMPORARY
name|TEMPORARY
argument_list|(
literal|4
argument_list|)
block|;
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|method|ReplicaState (int v)
specifier|private
name|ReplicaState
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|value
operator|=
name|v
expr_stmt|;
block|}
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|getState (int v)
specifier|public
specifier|static
name|ReplicaState
name|getState
parameter_list|(
name|int
name|v
parameter_list|)
block|{
return|return
name|ReplicaState
operator|.
name|values
argument_list|()
index|[
name|v
index|]
return|;
block|}
comment|/** Read from in */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|ReplicaState
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|values
argument_list|()
index|[
name|in
operator|.
name|readByte
argument_list|()
index|]
return|;
block|}
comment|/** Write to out */
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
name|writeByte
argument_list|(
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * States, which a block can go through while it is under construction.    */
DECL|enum|BlockUCState
specifier|static
specifier|public
enum|enum
name|BlockUCState
block|{
comment|/**      * Block construction completed.<br>      * The block has at least one {@link ReplicaState#FINALIZED} replica,      * and is not going to be modified.      */
DECL|enumConstant|COMPLETE
name|COMPLETE
block|,
comment|/**      * The block is under construction.<br>      * It has been recently allocated for write or append.      */
DECL|enumConstant|UNDER_CONSTRUCTION
name|UNDER_CONSTRUCTION
block|,
comment|/**      * The block is under recovery.<br>      * When a file lease expires its last block may not be {@link #COMPLETE}      * and needs to go through a recovery procedure,       * which synchronizes the existing replicas contents.      */
DECL|enumConstant|UNDER_RECOVERY
name|UNDER_RECOVERY
block|,
comment|/**      * The block is committed.<br>      * The client reported that all bytes are written to data-nodes      * with the given generation stamp and block length, but no       * {@link ReplicaState#FINALIZED}       * replicas has yet been reported by data-nodes themselves.      */
DECL|enumConstant|COMMITTED
name|COMMITTED
block|;   }
DECL|field|NAMENODE_LEASE_HOLDER
specifier|public
specifier|static
specifier|final
name|String
name|NAMENODE_LEASE_HOLDER
init|=
literal|"HDFS_NameNode"
decl_stmt|;
DECL|field|NAMENODE_LEASE_RECHECK_INTERVAL
specifier|public
specifier|static
specifier|final
name|long
name|NAMENODE_LEASE_RECHECK_INTERVAL
init|=
literal|2000
decl_stmt|;
block|}
end_class

end_unit

