begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.api
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
name|api
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
name|fs
operator|.
name|FileAlreadyExistsException
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
name|PathIsNotEmptyDirectoryException
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
name|PathNotFoundException
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
name|service
operator|.
name|Service
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
name|InvalidRecordException
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
comment|/**  * Registry Operations  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|RegistryOperations
specifier|public
interface|interface
name|RegistryOperations
extends|extends
name|Service
block|{
comment|/**    * Create a path.    *    * It is not an error if the path exists already, be it empty or not.    *    * The createParents flag also requests creating the parents.    * As entries in the registry can hold data while still having    * child entries, it is not an error if any of the parent path    * elements have service records.    *    * @param path path to create    * @param createParents also create the parents.    * @throws PathNotFoundException parent path is not in the registry.    * @throws InvalidPathnameException path name is invalid.    * @throws IOException Any other IO Exception.    * @return true if the path was created, false if it existed.    */
DECL|method|mknode (String path, boolean createParents)
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
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
function_decl|;
comment|/**    * Bind a path in the registry to a service record    * @param path path to service record    * @param record service record service record to create/update    * @param flags bind flags    * @throws PathNotFoundException the parent path does not exist    * @throws FileAlreadyExistsException path exists but create flags    * do not include "overwrite"    * @throws InvalidPathnameException path name is invalid.    * @throws IOException Any other IO Exception.    */
DECL|method|bind (String path, ServiceRecord record, int flags)
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
name|PathNotFoundException
throws|,
name|FileAlreadyExistsException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
function_decl|;
comment|/**    * Resolve the record at a path    * @param path path to an entry containing a {@link ServiceRecord}    * @return the record    * @throws PathNotFoundException path is not in the registry.    * @throws NoRecordException if there is not a service record    * @throws InvalidRecordException if there was a service record but it could    * not be parsed.    * @throws IOException Any other IO Exception    */
DECL|method|resolve (String path)
name|ServiceRecord
name|resolve
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|NoRecordException
throws|,
name|InvalidRecordException
throws|,
name|IOException
function_decl|;
comment|/**    * Get the status of a path    * @param path path to query    * @return the status of the path    * @throws PathNotFoundException path is not in the registry.    * @throws InvalidPathnameException the path is invalid.    * @throws IOException Any other IO Exception    */
DECL|method|stat (String path)
name|RegistryPathStatus
name|stat
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
function_decl|;
comment|/**    * Probe for a path existing.    * This is equivalent to {@link #stat(String)} with    * any failure downgraded to a    * @param path path to query    * @return true if the path was found    * @throws IOException    */
DECL|method|exists (String path)
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List all entries under a registry path, returning the relative names    * of the entries.    * @param path path to query    * @return a possibly empty list of the short path names of    * child entries.    * @throws PathNotFoundException    * @throws InvalidPathnameException    * @throws IOException    */
DECL|method|list (String path)
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
name|PathNotFoundException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
function_decl|;
comment|/**    * Delete a path.    *    * If the operation returns without an error then the entry has been    * deleted.    * @param path path delete recursively    * @param recursive recursive flag    * @throws PathNotFoundException path is not in the registry.    * @throws InvalidPathnameException the path is invalid.    * @throws PathIsNotEmptyDirectoryException path has child entries, but    * recursive is false.    * @throws IOException Any other IO Exception    *    */
DECL|method|delete (String path, boolean recursive)
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
name|PathNotFoundException
throws|,
name|PathIsNotEmptyDirectoryException
throws|,
name|InvalidPathnameException
throws|,
name|IOException
function_decl|;
comment|/**    * Add a new write access entry to be added to node permissions in all    * future write operations of a session connected to a secure registry.    *    * This does not grant the session any more rights: if it lacked any write    * access, it will still be unable to manipulate the registry.    *    * In an insecure cluster, this operation has no effect.    * @param id ID to use    * @param pass password    * @return true if the accessor was added: that is, the registry connection    * uses permissions to manage access    * @throws IOException on any failure to build the digest    */
DECL|method|addWriteAccessor (String id, String pass)
name|boolean
name|addWriteAccessor
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|pass
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Clear all write accessors.    *    * At this point all standard permissions/ACLs are retained,    * including any set on behalf of the user    * Only  accessors added via {@link #addWriteAccessor(String, String)}    * are removed.    */
DECL|method|clearWriteAccessors ()
specifier|public
name|void
name|clearWriteAccessors
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

