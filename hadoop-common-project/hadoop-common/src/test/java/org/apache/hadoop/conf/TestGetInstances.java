begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
DECL|class|TestGetInstances
specifier|public
class|class
name|TestGetInstances
block|{
DECL|interface|SampleInterface
interface|interface
name|SampleInterface
block|{}
DECL|interface|ChildInterface
interface|interface
name|ChildInterface
extends|extends
name|SampleInterface
block|{}
DECL|class|SampleClass
specifier|static
class|class
name|SampleClass
implements|implements
name|SampleInterface
block|{
DECL|method|SampleClass ()
name|SampleClass
parameter_list|()
block|{}
block|}
DECL|class|AnotherClass
specifier|static
class|class
name|AnotherClass
implements|implements
name|ChildInterface
block|{
DECL|method|AnotherClass ()
name|AnotherClass
parameter_list|()
block|{}
block|}
comment|/**    * Makes sure<code>Configuration.getInstances()</code> returns    * instances of the required type.    */
annotation|@
name|Test
DECL|method|testGetInstances ()
specifier|public
name|void
name|testGetInstances
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SampleInterface
argument_list|>
name|classes
init|=
name|conf
operator|.
name|getInstances
argument_list|(
literal|"no.such.property"
argument_list|,
name|SampleInterface
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|classes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"empty.property"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|classes
operator|=
name|conf
operator|.
name|getInstances
argument_list|(
literal|"empty.property"
argument_list|,
name|SampleInterface
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|classes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setStrings
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AnotherClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|classes
operator|=
name|conf
operator|.
name|getInstances
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleInterface
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|classes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|conf
operator|.
name|setStrings
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AnotherClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|String
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|getInstances
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleInterface
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"java.lang.String does not implement SampleInterface"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{}
try|try
block|{
name|conf
operator|.
name|setStrings
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|AnotherClass
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"no.such.Class"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|getInstances
argument_list|(
literal|"some.classes"
argument_list|,
name|SampleInterface
operator|.
name|class
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"no.such.Class does not exist"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{}
block|}
block|}
end_class

end_unit

