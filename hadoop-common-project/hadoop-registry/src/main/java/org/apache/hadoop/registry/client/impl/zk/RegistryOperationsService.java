begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.impl.zk
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|impl
operator|.
name|zk
package|;
end_package

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
name|Preconditions
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|BindFlags
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryTypeUtils
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryUtils
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
name|registry
operator|.
name|client
operator|.
name|binding
operator|.
name|RegistryPathUtils
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|InvalidPathnameException
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|NoRecordException
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|RegistryPathStatus
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|CreateMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|ACL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|zookeeper
operator|.
name|data
operator|.
name|Stat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * The Registry operations service.  *<p>  * This service implements the {@link RegistryOperations}  * API by mapping the commands to zookeeper operations, and translating  * results and exceptions back into those specified by the API.  *<p>  * Factory methods should hide the detail that this has been implemented via  * the {@link CuratorService} by returning it cast to that  * {@link RegistryOperations} interface, rather than this implementation class.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|RegistryOperationsService
specifier|public
class|class
name|RegistryOperationsService
extends|extends
name|CuratorService
implements|implements
name|RegistryOperations
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RegistryOperationsService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceRecordMarshal
specifier|private
specifier|final
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
name|serviceRecordMarshal
init|=
operator|new
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
argument_list|()
decl_stmt|;
DECL|method|RegistryOperationsService (String name)
specifier|public
name|RegistryOperationsService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|RegistryOperationsService ()
specifier|public
name|RegistryOperationsService
parameter_list|()
block|{
name|this
argument_list|(
literal|"RegistryOperationsService"
argument_list|)
expr_stmt|;
block|}
DECL|method|RegistryOperationsService (String name, RegistryBindingSource bindingSource)
specifier|public
name|RegistryOperationsService
parameter_list|(
name|String
name|name
parameter_list|,
name|RegistryBindingSource
name|bindingSource
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bindingSource
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the aggregate set of ACLs the client should use    * to create directories    * @return the ACL list    */
DECL|method|getClientAcls ()
specifier|public
name|List
argument_list|<
name|ACL
argument_list|>
name|getClientAcls
parameter_list|()
block|{
return|return
name|getRegistrySecurity
argument_list|()
operator|.
name|getClientACLs
argument_list|()
return|;
block|}
comment|/**    * Validate a path    * @param path path to validate    * @throws InvalidPathnameException if a path is considered invalid    */
DECL|method|validatePath (String path)
specifier|protected
name|void
name|validatePath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|InvalidPathnameException
block|{
comment|// currently no checks are performed
block|}
annotation|@
name|Override
DECL|method|mknode (String path, boolean createParents)
specifier|public
name|boolean
name|mknode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|createParents
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|zkMkPath
argument_list|(
name|path
argument_list|,
name|CreateMode
operator|.
name|PERSISTENT
argument_list|,
name|createParents
argument_list|,
name|getClientAcls
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|bind (String path, ServiceRecord record, int flags)
specifier|public
name|void
name|bind
parameter_list|(
name|String
name|path
parameter_list|,
name|ServiceRecord
name|record
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|record
operator|!=
literal|null
argument_list|,
literal|"null record"
argument_list|)
expr_stmt|;
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// validate the record before putting it
name|RegistryTypeUtils
operator|.
name|validateServiceRecord
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Bound at {} : ServiceRecord = {}"
argument_list|,
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
block|}
name|CreateMode
name|mode
init|=
name|CreateMode
operator|.
name|PERSISTENT
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|serviceRecordMarshal
operator|.
name|toBytes
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|zkSet
argument_list|(
name|path
argument_list|,
name|mode
argument_list|,
name|bytes
argument_list|,
name|getClientAcls
argument_list|()
argument_list|,
operator|(
operator|(
name|flags
operator|&
name|BindFlags
operator|.
name|OVERWRITE
operator|)
operator|!=
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|resolve (String path)
specifier|public
name|ServiceRecord
name|resolve
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
name|zkRead
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|ServiceRecord
name|record
init|=
name|serviceRecordMarshal
operator|.
name|fromBytes
argument_list|(
name|path
argument_list|,
name|bytes
argument_list|,
name|ServiceRecord
operator|.
name|RECORD_TYPE
argument_list|)
decl_stmt|;
name|RegistryTypeUtils
operator|.
name|validateServiceRecord
argument_list|(
name|path
argument_list|,
name|record
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
annotation|@
name|Override
DECL|method|exists (String path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|zkPathExists
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stat (String path)
specifier|public
name|RegistryPathStatus
name|stat
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Stat
name|stat
init|=
name|zkStat
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|RegistryPathUtils
operator|.
name|lastPathEntry
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|RegistryPathStatus
name|status
init|=
operator|new
name|RegistryPathStatus
argument_list|(
name|name
argument_list|,
name|stat
operator|.
name|getCtime
argument_list|()
argument_list|,
name|stat
operator|.
name|getDataLength
argument_list|()
argument_list|,
name|stat
operator|.
name|getNumChildren
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stat {} => {}"
argument_list|,
name|path
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|list (String path)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|list
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|zkList
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (String path, boolean recursive)
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
block|{
name|validatePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|zkDelete
argument_list|(
name|path
argument_list|,
name|recursive
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

