begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
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
name|conf
operator|.
name|Configuration
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
name|test
operator|.
name|HTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestServerConstructor
specifier|public
class|class
name|TestServerConstructor
extends|extends
name|HTestCase
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|constructorFailParams ()
specifier|public
specifier|static
name|Collection
name|constructorFailParams
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|""
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|""
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"foo"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|""
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"foo"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|null
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|""
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|"foo"
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|null
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|""
block|,
literal|null
block|}
block|,
block|{
literal|"server"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|"/tmp"
block|,
literal|"foo"
block|,
literal|null
block|}
block|}
argument_list|)
return|;
block|}
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|homeDir
specifier|private
name|String
name|homeDir
decl_stmt|;
DECL|field|configDir
specifier|private
name|String
name|configDir
decl_stmt|;
DECL|field|logDir
specifier|private
name|String
name|logDir
decl_stmt|;
DECL|field|tempDir
specifier|private
name|String
name|tempDir
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|TestServerConstructor (String name, String homeDir, String configDir, String logDir, String tempDir, Configuration conf)
specifier|public
name|TestServerConstructor
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|homeDir
parameter_list|,
name|String
name|configDir
parameter_list|,
name|String
name|logDir
parameter_list|,
name|String
name|tempDir
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|homeDir
operator|=
name|homeDir
expr_stmt|;
name|this
operator|.
name|configDir
operator|=
name|configDir
expr_stmt|;
name|this
operator|.
name|logDir
operator|=
name|logDir
expr_stmt|;
name|this
operator|.
name|tempDir
operator|=
name|tempDir
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|constructorFail ()
specifier|public
name|void
name|constructorFail
parameter_list|()
block|{
operator|new
name|Server
argument_list|(
name|name
argument_list|,
name|homeDir
argument_list|,
name|configDir
argument_list|,
name|logDir
argument_list|,
name|tempDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

