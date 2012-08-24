begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|conf
operator|.
name|Configuration
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestCommandFactory
specifier|public
class|class
name|TestCommandFactory
block|{
DECL|field|factory
specifier|static
name|CommandFactory
name|factory
decl_stmt|;
DECL|field|conf
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|method|registerCommands (CommandFactory factory)
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{   }
annotation|@
name|Before
DECL|method|testSetup ()
specifier|public
name|void
name|testSetup
parameter_list|()
block|{
name|factory
operator|=
operator|new
name|CommandFactory
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRegistration ()
specifier|public
name|void
name|testRegistration
parameter_list|()
block|{
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{}
argument_list|,
name|factory
operator|.
name|getNames
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|registerCommands
argument_list|(
name|TestRegistrar
operator|.
name|class
argument_list|)
expr_stmt|;
name|String
index|[]
name|names
init|=
name|factory
operator|.
name|getNames
argument_list|()
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tc1"
block|,
literal|"tc2"
block|,
literal|"tc2.1"
block|}
argument_list|,
name|names
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|TestCommand3
operator|.
name|class
argument_list|,
literal|"tc3"
argument_list|)
expr_stmt|;
name|names
operator|=
name|factory
operator|.
name|getNames
argument_list|()
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tc1"
block|,
literal|"tc2"
block|,
literal|"tc2.1"
block|,
literal|"tc3"
block|}
argument_list|,
name|names
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|TestCommand4
operator|.
name|class
argument_list|,
operator|(
operator|new
name|TestCommand4
argument_list|()
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|names
operator|=
name|factory
operator|.
name|getNames
argument_list|()
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"tc1"
block|,
literal|"tc2"
block|,
literal|"tc2.1"
block|,
literal|"tc3"
block|,
literal|"tc4"
block|}
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetInstances ()
specifier|public
name|void
name|testGetInstances
parameter_list|()
block|{
name|factory
operator|.
name|registerCommands
argument_list|(
name|TestRegistrar
operator|.
name|class
argument_list|)
expr_stmt|;
name|Command
name|instance
decl_stmt|;
name|instance
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
literal|"blarg"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|instance
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
literal|"tc1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestCommand1
operator|.
name|class
argument_list|,
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tc1"
argument_list|,
name|instance
operator|.
name|getCommandName
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
literal|"tc2"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestCommand2
operator|.
name|class
argument_list|,
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tc2"
argument_list|,
name|instance
operator|.
name|getCommandName
argument_list|()
argument_list|)
expr_stmt|;
name|instance
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
literal|"tc2.1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestCommand2
operator|.
name|class
argument_list|,
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tc2.1"
argument_list|,
name|instance
operator|.
name|getCommandName
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|TestCommand4
operator|.
name|class
argument_list|,
literal|"tc4"
argument_list|)
expr_stmt|;
name|instance
operator|=
name|factory
operator|.
name|getInstance
argument_list|(
literal|"tc4"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestCommand4
operator|.
name|class
argument_list|,
name|instance
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tc4"
argument_list|,
name|instance
operator|.
name|getCommandName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|usage
init|=
name|instance
operator|.
name|getUsage
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"-tc4 tc4_usage"
argument_list|,
name|usage
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"tc4_description"
argument_list|,
name|instance
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestRegistrar
specifier|static
class|class
name|TestRegistrar
block|{
DECL|method|registerCommands (CommandFactory factory)
specifier|public
specifier|static
name|void
name|registerCommands
parameter_list|(
name|CommandFactory
name|factory
parameter_list|)
block|{
name|factory
operator|.
name|addClass
argument_list|(
name|TestCommand1
operator|.
name|class
argument_list|,
literal|"tc1"
argument_list|)
expr_stmt|;
name|factory
operator|.
name|addClass
argument_list|(
name|TestCommand2
operator|.
name|class
argument_list|,
literal|"tc2"
argument_list|,
literal|"tc2.1"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestCommand1
specifier|static
class|class
name|TestCommand1
extends|extends
name|FsCommand
block|{}
DECL|class|TestCommand2
specifier|static
class|class
name|TestCommand2
extends|extends
name|FsCommand
block|{}
DECL|class|TestCommand3
specifier|static
class|class
name|TestCommand3
extends|extends
name|FsCommand
block|{}
DECL|class|TestCommand4
specifier|static
class|class
name|TestCommand4
extends|extends
name|FsCommand
block|{
DECL|field|NAME
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"tc4"
decl_stmt|;
DECL|field|USAGE
specifier|static
specifier|final
name|String
name|USAGE
init|=
literal|"tc4_usage"
decl_stmt|;
DECL|field|DESCRIPTION
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"tc4_description"
decl_stmt|;
block|}
block|}
end_class

end_unit

