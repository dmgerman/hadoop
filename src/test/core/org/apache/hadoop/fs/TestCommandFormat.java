begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|shell
operator|.
name|CommandFormat
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
name|shell
operator|.
name|CommandFormat
operator|.
name|NotEnoughArgumentsException
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
name|shell
operator|.
name|CommandFormat
operator|.
name|TooManyArgumentsException
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
name|shell
operator|.
name|CommandFormat
operator|.
name|UnknownOptionException
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

begin_comment
comment|/**  * This class tests the command line parsing  */
end_comment

begin_class
DECL|class|TestCommandFormat
specifier|public
class|class
name|TestCommandFormat
block|{
DECL|field|args
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|args
decl_stmt|;
DECL|field|expectedArgs
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|expectedArgs
decl_stmt|;
DECL|field|expectedOpts
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|expectedOpts
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|args
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|expectedOpts
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|expectedArgs
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoArgs ()
specifier|public
name|void
name|testNoArgs
parameter_list|()
block|{
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneArg ()
specifier|public
name|void
name|testOneArg
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoArgs ()
specifier|public
name|void
name|testTwoArgs
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|3
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOneOpt ()
specifier|public
name|void
name|testOneOpt
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"-a"
argument_list|)
expr_stmt|;
name|expectedOpts
operator|=
name|setOf
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|UnknownOptionException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTwoOpts ()
specifier|public
name|void
name|testTwoOpts
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"-a"
argument_list|,
literal|"-b"
argument_list|)
expr_stmt|;
name|expectedOpts
operator|=
name|setOf
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|UnknownOptionException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptArg ()
specifier|public
name|void
name|testOptArg
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"-a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|expectedOpts
operator|=
name|setOf
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|UnknownOptionException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testArgOpt ()
specifier|public
name|void
name|testArgOpt
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"b"
argument_list|,
literal|"-a"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"b"
argument_list|,
literal|"-a"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptStopOptArg ()
specifier|public
name|void
name|testOptStopOptArg
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"-a"
argument_list|,
literal|"--"
argument_list|,
literal|"-b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|expectedOpts
operator|=
name|setOf
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"-b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|UnknownOptionException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptDashArg ()
specifier|public
name|void
name|testOptDashArg
parameter_list|()
block|{
name|args
operator|=
name|listOf
argument_list|(
literal|"-b"
argument_list|,
literal|"-"
argument_list|,
literal|"-c"
argument_list|)
expr_stmt|;
name|expectedOpts
operator|=
name|setOf
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|expectedArgs
operator|=
name|listOf
argument_list|(
literal|"-"
argument_list|,
literal|"-c"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|UnknownOptionException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|TooManyArgumentsException
operator|.
name|class
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
literal|null
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|checkArgLimits
argument_list|(
name|NotEnoughArgumentsException
operator|.
name|class
argument_list|,
literal|3
argument_list|,
literal|4
argument_list|,
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOldArgsWithIndex ()
specifier|public
name|void
name|testOldArgsWithIndex
parameter_list|()
block|{
name|String
index|[]
name|arrayArgs
init|=
operator|new
name|String
index|[]
block|{
literal|"ignore"
block|,
literal|"-a"
block|,
literal|"b"
block|,
literal|"-c"
block|}
decl_stmt|;
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
literal|9
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parsedArgs
init|=
name|cf
operator|.
name|parse
argument_list|(
name|arrayArgs
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|setOf
argument_list|()
argument_list|,
name|cf
operator|.
name|getOpts
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|listOf
argument_list|(
literal|"ignore"
argument_list|,
literal|"-a"
argument_list|,
literal|"b"
argument_list|,
literal|"-c"
argument_list|)
argument_list|,
name|parsedArgs
argument_list|)
expr_stmt|;
block|}
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
literal|9
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parsedArgs
init|=
name|cf
operator|.
name|parse
argument_list|(
name|arrayArgs
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|setOf
argument_list|(
literal|"a"
argument_list|)
argument_list|,
name|cf
operator|.
name|getOpts
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|listOf
argument_list|(
literal|"b"
argument_list|,
literal|"-c"
argument_list|)
argument_list|,
name|parsedArgs
argument_list|)
expr_stmt|;
block|}
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
literal|0
argument_list|,
literal|9
argument_list|,
literal|"a"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parsedArgs
init|=
name|cf
operator|.
name|parse
argument_list|(
name|arrayArgs
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|setOf
argument_list|()
argument_list|,
name|cf
operator|.
name|getOpts
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|listOf
argument_list|(
literal|"b"
argument_list|,
literal|"-c"
argument_list|)
argument_list|,
name|parsedArgs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkArgLimits ( Class<? extends IllegalArgumentException> expectedErr, int min, int max, String ... opts)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CommandFormat
name|checkArgLimits
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|IllegalArgumentException
argument_list|>
name|expectedErr
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|String
modifier|...
name|opts
parameter_list|)
block|{
name|CommandFormat
name|cf
init|=
operator|new
name|CommandFormat
argument_list|(
name|min
argument_list|,
name|max
argument_list|,
name|opts
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|parsedArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|cfError
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cf
operator|.
name|parse
argument_list|(
name|parsedArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|cfError
operator|=
name|e
operator|.
name|getClass
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedErr
argument_list|,
name|cfError
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedErr
operator|==
literal|null
condition|)
block|{
name|assertEquals
argument_list|(
name|expectedArgs
argument_list|,
name|parsedArgs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedOpts
argument_list|,
name|cf
operator|.
name|getOpts
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cf
return|;
block|}
comment|// Don't use generics to avoid warning:
comment|// unchecked generic array creation of type T[] for varargs parameter
DECL|method|listOf (String .... objects)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|listOf
parameter_list|(
name|String
modifier|...
name|objects
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|objects
argument_list|)
return|;
block|}
DECL|method|setOf (String .... objects)
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|setOf
parameter_list|(
name|String
modifier|...
name|objects
parameter_list|)
block|{
return|return
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|listOf
argument_list|(
name|objects
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

