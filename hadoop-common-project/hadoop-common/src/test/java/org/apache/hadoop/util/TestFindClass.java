begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|util
operator|.
name|FindClass
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
name|util
operator|.
name|ToolRunner
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

begin_comment
comment|/**  * Test the find class logic  */
end_comment

begin_class
DECL|class|TestFindClass
specifier|public
class|class
name|TestFindClass
extends|extends
name|Assert
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
name|TestFindClass
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOG4J_PROPERTIES
specifier|public
specifier|static
specifier|final
name|String
name|LOG4J_PROPERTIES
init|=
literal|"log4j.properties"
decl_stmt|;
comment|/**    * Run the tool runner instance    * @param expected expected return code    * @param args a list of arguments    * @throws Exception on any falure that is not handled earlier    */
DECL|method|run (int expected, String... args)
specifier|private
name|void
name|run
parameter_list|(
name|int
name|expected
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|result
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|FindClass
argument_list|()
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUsage ()
specifier|public
name|void
name|testUsage
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_USAGE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFindsResource ()
specifier|public
name|void
name|testFindsResource
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_RESOURCE
argument_list|,
literal|"org/apache/hadoop/util/TestFindClass.class"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailsNoSuchResource ()
specifier|public
name|void
name|testFailsNoSuchResource
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_NOT_FOUND
argument_list|,
name|FindClass
operator|.
name|A_RESOURCE
argument_list|,
literal|"org/apache/hadoop/util/ThereIsNoSuchClass.class"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadFindsSelf ()
specifier|public
name|void
name|testLoadFindsSelf
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_LOAD
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadFailsNoSuchClass ()
specifier|public
name|void
name|testLoadFailsNoSuchClass
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_NOT_FOUND
argument_list|,
name|FindClass
operator|.
name|A_LOAD
argument_list|,
literal|"org.apache.hadoop.util.ThereIsNoSuchClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadWithErrorInStaticInit ()
specifier|public
name|void
name|testLoadWithErrorInStaticInit
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_LOAD_FAILED
argument_list|,
name|FindClass
operator|.
name|A_LOAD
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$FailInStaticInit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateHandlesBadToString ()
specifier|public
name|void
name|testCreateHandlesBadToString
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$BadToStringClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreatesClass ()
specifier|public
name|void
name|testCreatesClass
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFailsInStaticInit ()
specifier|public
name|void
name|testCreateFailsInStaticInit
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_LOAD_FAILED
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$FailInStaticInit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFailsInConstructor ()
specifier|public
name|void
name|testCreateFailsInConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_CREATE_FAILED
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$FailInConstructor"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFailsNoEmptyConstructor ()
specifier|public
name|void
name|testCreateFailsNoEmptyConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_CREATE_FAILED
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$NoEmptyConstructor"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadPrivateClass ()
specifier|public
name|void
name|testLoadPrivateClass
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_LOAD
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$PrivateClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFailsPrivateClass ()
specifier|public
name|void
name|testCreateFailsPrivateClass
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_CREATE_FAILED
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$PrivateClass"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCreateFailsInPrivateConstructor ()
specifier|public
name|void
name|testCreateFailsInPrivateConstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|E_CREATE_FAILED
argument_list|,
name|FindClass
operator|.
name|A_CREATE
argument_list|,
literal|"org.apache.hadoop.util.TestFindClass$PrivateConstructor"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLoadFindsLog4J ()
specifier|public
name|void
name|testLoadFindsLog4J
parameter_list|()
throws|throws
name|Throwable
block|{
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_RESOURCE
argument_list|,
name|LOG4J_PROPERTIES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UseOfSystemOutOrSystemErr"
argument_list|)
annotation|@
name|Test
DECL|method|testPrintLog4J ()
specifier|public
name|void
name|testPrintLog4J
parameter_list|()
throws|throws
name|Throwable
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|FindClass
operator|.
name|setOutputStreams
argument_list|(
name|out
argument_list|,
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|run
argument_list|(
name|FindClass
operator|.
name|SUCCESS
argument_list|,
name|FindClass
operator|.
name|A_PRINTRESOURCE
argument_list|,
name|LOG4J_PROPERTIES
argument_list|)
expr_stmt|;
comment|//here the content should be done
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|String
name|body
init|=
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF8"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|LOG4J_PROPERTIES
operator|+
literal|" =\n"
operator|+
name|body
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|body
operator|.
name|contains
argument_list|(
literal|"Apache"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * trigger a divide by zero fault in the static init    */
DECL|class|FailInStaticInit
specifier|public
specifier|static
class|class
name|FailInStaticInit
block|{
static|static
block|{
name|int
name|x
init|=
literal|0
decl_stmt|;
name|int
name|y
init|=
literal|1
operator|/
name|x
decl_stmt|;
block|}
block|}
comment|/**    * trigger a divide by zero fault in the constructor    */
DECL|class|FailInConstructor
specifier|public
specifier|static
class|class
name|FailInConstructor
block|{
DECL|method|FailInConstructor ()
specifier|public
name|FailInConstructor
parameter_list|()
block|{
name|int
name|x
init|=
literal|0
decl_stmt|;
name|int
name|y
init|=
literal|1
operator|/
name|x
decl_stmt|;
block|}
block|}
comment|/**    * A class with no parameterless constructor -expect creation to fail    */
DECL|class|NoEmptyConstructor
specifier|public
specifier|static
class|class
name|NoEmptyConstructor
block|{
DECL|method|NoEmptyConstructor (String text)
specifier|public
name|NoEmptyConstructor
parameter_list|(
name|String
name|text
parameter_list|)
block|{     }
block|}
comment|/**    * This has triggers an NPE in the toString() method; checks the logging    * code handles this.    */
DECL|class|BadToStringClass
specifier|public
specifier|static
class|class
name|BadToStringClass
block|{
DECL|method|BadToStringClass ()
specifier|public
name|BadToStringClass
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"oops"
argument_list|)
throw|;
block|}
block|}
comment|/**    * This has a private constructor    * -creating it will trigger an IllegalAccessException    */
DECL|class|PrivateClass
specifier|public
specifier|static
class|class
name|PrivateClass
block|{
DECL|method|PrivateClass ()
specifier|private
name|PrivateClass
parameter_list|()
block|{     }
block|}
comment|/**    * This has a private constructor    * -creating it will trigger an IllegalAccessException    */
DECL|class|PrivateConstructor
specifier|public
specifier|static
class|class
name|PrivateConstructor
block|{
DECL|method|PrivateConstructor ()
specifier|private
name|PrivateConstructor
parameter_list|()
block|{     }
block|}
block|}
end_class

end_unit

