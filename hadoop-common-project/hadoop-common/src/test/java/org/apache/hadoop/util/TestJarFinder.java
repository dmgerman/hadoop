begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_class
DECL|class|TestJarFinder
specifier|public
class|class
name|TestJarFinder
block|{
annotation|@
name|Test
DECL|method|testAppend ()
specifier|public
name|void
name|testAppend
parameter_list|()
throws|throws
name|Exception
block|{
comment|//picking a class that is for sure in a JAR in the classpath
name|String
name|jar
init|=
name|JarFinder
operator|.
name|getJar
argument_list|(
name|LogFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|jar
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
comment|//picking a class that is for sure in a directory in the classpath
comment|//in this case the JAR is created on the fly
name|jar
operator|=
name|JarFinder
operator|.
name|getJar
argument_list|(
name|TestJarFinder
operator|.
name|class
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
operator|new
name|File
argument_list|(
name|jar
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

