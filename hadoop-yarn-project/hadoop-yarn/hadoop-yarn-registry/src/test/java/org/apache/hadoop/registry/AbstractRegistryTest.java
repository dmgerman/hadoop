begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
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
name|types
operator|.
name|yarn
operator|.
name|PersistencePolicies
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
name|hadoop
operator|.
name|registry
operator|.
name|server
operator|.
name|services
operator|.
name|RegistryAdminService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_class
DECL|class|AbstractRegistryTest
specifier|public
class|class
name|AbstractRegistryTest
extends|extends
name|AbstractZKRegistryTest
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
name|AbstractRegistryTest
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|registry
specifier|protected
name|RegistryAdminService
name|registry
decl_stmt|;
DECL|field|operations
specifier|protected
name|RegistryOperations
name|operations
decl_stmt|;
annotation|@
name|Before
DECL|method|setupRegistry ()
specifier|public
name|void
name|setupRegistry
parameter_list|()
throws|throws
name|IOException
block|{
name|registry
operator|=
operator|new
name|RegistryAdminService
argument_list|(
literal|"yarnRegistry"
argument_list|)
expr_stmt|;
name|operations
operator|=
name|registry
expr_stmt|;
name|registry
operator|.
name|init
argument_list|(
name|createRegistryConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|registry
operator|.
name|start
argument_list|()
expr_stmt|;
name|operations
operator|.
name|delete
argument_list|(
literal|"/"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|registry
operator|.
name|createRootRegistryPaths
argument_list|()
expr_stmt|;
name|addToTeardown
argument_list|(
name|registry
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a service entry with the sample endpoints, and put it    * at the destination    * @param path path    * @param createFlags flags    * @return the record    * @throws IOException on a failure    */
DECL|method|putExampleServiceEntry (String path, int createFlags)
specifier|protected
name|ServiceRecord
name|putExampleServiceEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|createFlags
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
name|putExampleServiceEntry
argument_list|(
name|path
argument_list|,
name|createFlags
argument_list|,
name|PersistencePolicies
operator|.
name|PERMANENT
argument_list|)
return|;
block|}
comment|/**    * Create a service entry with the sample endpoints, and put it    * at the destination    * @param path path    * @param createFlags flags    * @return the record    * @throws IOException on a failure    */
DECL|method|putExampleServiceEntry (String path, int createFlags, String persistence)
specifier|protected
name|ServiceRecord
name|putExampleServiceEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|createFlags
parameter_list|,
name|String
name|persistence
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|ServiceRecord
name|record
init|=
name|buildExampleServiceEntry
argument_list|(
name|persistence
argument_list|)
decl_stmt|;
name|registry
operator|.
name|mknode
argument_list|(
name|RegistryPathUtils
operator|.
name|parentOf
argument_list|(
name|path
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|operations
operator|.
name|bind
argument_list|(
name|path
argument_list|,
name|record
argument_list|,
name|createFlags
argument_list|)
expr_stmt|;
return|return
name|record
return|;
block|}
comment|/**    * Assert a path exists    * @param path path in the registry    * @throws IOException    */
DECL|method|assertPathExists (String path)
specifier|public
name|void
name|assertPathExists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|operations
operator|.
name|stat
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * assert that a path does not exist    * @param path path in the registry    * @throws IOException    */
DECL|method|assertPathNotFound (String path)
specifier|public
name|void
name|assertPathNotFound
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|operations
operator|.
name|stat
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Path unexpectedly found: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{      }
block|}
comment|/**    * Assert that a path resolves to a service record    * @param path path in the registry    * @throws IOException    */
DECL|method|assertResolves (String path)
specifier|public
name|void
name|assertResolves
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|operations
operator|.
name|resolve
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

