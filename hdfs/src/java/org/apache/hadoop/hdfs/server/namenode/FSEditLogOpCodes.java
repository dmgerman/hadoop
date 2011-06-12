begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * Op codes for edits file  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|FSEditLogOpCodes
specifier|public
enum|enum
name|FSEditLogOpCodes
block|{
comment|// last op code in file
DECL|enumConstant|OP_INVALID
name|OP_INVALID
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|1
argument_list|)
block|,
DECL|enumConstant|OP_ADD
name|OP_ADD
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enumConstant|OP_RENAME_OLD
name|OP_RENAME_OLD
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
comment|// deprecated operation
DECL|enumConstant|OP_DELETE
name|OP_DELETE
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|,
DECL|enumConstant|OP_MKDIR
name|OP_MKDIR
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
block|,
DECL|enumConstant|OP_SET_REPLICATION
name|OP_SET_REPLICATION
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
block|,
DECL|enumConstant|Deprecated
DECL|enumConstant|OP_DATANODE_ADD
annotation|@
name|Deprecated
name|OP_DATANODE_ADD
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
block|,
DECL|enumConstant|Deprecated
DECL|enumConstant|OP_DATANODE_REMOVE
annotation|@
name|Deprecated
name|OP_DATANODE_REMOVE
argument_list|(
operator|(
name|byte
operator|)
literal|6
argument_list|)
block|,
DECL|enumConstant|OP_SET_PERMISSIONS
name|OP_SET_PERMISSIONS
argument_list|(
operator|(
name|byte
operator|)
literal|7
argument_list|)
block|,
DECL|enumConstant|OP_SET_OWNER
name|OP_SET_OWNER
argument_list|(
operator|(
name|byte
operator|)
literal|8
argument_list|)
block|,
DECL|enumConstant|OP_CLOSE
name|OP_CLOSE
argument_list|(
operator|(
name|byte
operator|)
literal|9
argument_list|)
block|,
DECL|enumConstant|OP_SET_GENSTAMP
name|OP_SET_GENSTAMP
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
block|,
DECL|enumConstant|OP_SET_NS_QUOTA
name|OP_SET_NS_QUOTA
argument_list|(
operator|(
name|byte
operator|)
literal|11
argument_list|)
block|,
comment|// obsolete
DECL|enumConstant|OP_CLEAR_NS_QUOTA
name|OP_CLEAR_NS_QUOTA
argument_list|(
operator|(
name|byte
operator|)
literal|12
argument_list|)
block|,
comment|// obsolete
DECL|enumConstant|OP_TIMES
name|OP_TIMES
argument_list|(
operator|(
name|byte
operator|)
literal|13
argument_list|)
block|,
comment|// set atime, mtime
DECL|enumConstant|OP_SET_QUOTA
name|OP_SET_QUOTA
argument_list|(
operator|(
name|byte
operator|)
literal|14
argument_list|)
block|,
DECL|enumConstant|OP_RENAME
name|OP_RENAME
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|)
block|,
comment|// filecontext rename
DECL|enumConstant|OP_CONCAT_DELETE
name|OP_CONCAT_DELETE
argument_list|(
operator|(
name|byte
operator|)
literal|16
argument_list|)
block|,
comment|// concat files
DECL|enumConstant|OP_SYMLINK
name|OP_SYMLINK
argument_list|(
operator|(
name|byte
operator|)
literal|17
argument_list|)
block|,
DECL|enumConstant|OP_GET_DELEGATION_TOKEN
name|OP_GET_DELEGATION_TOKEN
argument_list|(
operator|(
name|byte
operator|)
literal|18
argument_list|)
block|,
DECL|enumConstant|OP_RENEW_DELEGATION_TOKEN
name|OP_RENEW_DELEGATION_TOKEN
argument_list|(
operator|(
name|byte
operator|)
literal|19
argument_list|)
block|,
DECL|enumConstant|OP_CANCEL_DELEGATION_TOKEN
name|OP_CANCEL_DELEGATION_TOKEN
argument_list|(
operator|(
name|byte
operator|)
literal|20
argument_list|)
block|,
DECL|enumConstant|OP_UPDATE_MASTER_KEY
name|OP_UPDATE_MASTER_KEY
argument_list|(
operator|(
name|byte
operator|)
literal|21
argument_list|)
block|,
DECL|enumConstant|OP_REASSIGN_LEASE
name|OP_REASSIGN_LEASE
argument_list|(
operator|(
name|byte
operator|)
literal|22
argument_list|)
block|,
comment|// must be same as NamenodeProtocol.JA_JSPOOL_START
DECL|enumConstant|OP_JSPOOL_START
name|OP_JSPOOL_START
argument_list|(
operator|(
name|byte
operator|)
literal|102
argument_list|)
block|,
comment|// must be same as NamenodeProtocol.JA_CHECKPOINT_TIME
DECL|enumConstant|OP_CHECKPOINT_TIME
name|OP_CHECKPOINT_TIME
argument_list|(
operator|(
name|byte
operator|)
literal|103
argument_list|)
block|;
DECL|field|opCode
specifier|private
name|byte
name|opCode
decl_stmt|;
comment|/**    * Constructor    *    * @param opCode byte value of constructed enum    */
DECL|method|FSEditLogOpCodes (byte opCode)
name|FSEditLogOpCodes
parameter_list|(
name|byte
name|opCode
parameter_list|)
block|{
name|this
operator|.
name|opCode
operator|=
name|opCode
expr_stmt|;
block|}
comment|/**    * return the byte value of the enum    *    * @return the byte value of the enum    */
DECL|method|getOpCode ()
specifier|public
name|byte
name|getOpCode
parameter_list|()
block|{
return|return
name|opCode
return|;
block|}
DECL|field|byteToEnum
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Byte
argument_list|,
name|FSEditLogOpCodes
argument_list|>
name|byteToEnum
init|=
operator|new
name|HashMap
argument_list|<
name|Byte
argument_list|,
name|FSEditLogOpCodes
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
comment|// initialize byte to enum map
for|for
control|(
name|FSEditLogOpCodes
name|opCode
range|:
name|values
argument_list|()
control|)
name|byteToEnum
operator|.
name|put
argument_list|(
name|opCode
operator|.
name|getOpCode
argument_list|()
argument_list|,
name|opCode
argument_list|)
expr_stmt|;
block|}
comment|/**    * Converts byte to FSEditLogOpCodes enum value    *    * @param opCode get enum for this opCode    * @return enum with byte value of opCode    */
DECL|method|fromByte (byte opCode)
specifier|public
specifier|static
name|FSEditLogOpCodes
name|fromByte
parameter_list|(
name|byte
name|opCode
parameter_list|)
block|{
return|return
name|byteToEnum
operator|.
name|get
argument_list|(
name|opCode
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

