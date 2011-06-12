begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|join
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileSystem
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
name|Path
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
name|io
operator|.
name|NullWritable
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
name|mapreduce
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
name|mapreduce
operator|.
name|MapReduceTestUtil
operator|.
name|Fake_RR
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
name|mapreduce
operator|.
name|task
operator|.
name|TaskAttemptContextImpl
import|;
end_import

begin_class
DECL|class|TestWrappedRRClassloader
specifier|public
class|class
name|TestWrappedRRClassloader
extends|extends
name|TestCase
block|{
comment|/**    * Tests the class loader set by     * {@link Configuration#setClassLoader(ClassLoader)}    * is inherited by any {@link WrappedRecordReader}s created by    * {@link CompositeRecordReader}    */
DECL|method|testClassLoader ()
specifier|public
name|void
name|testClassLoader
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
name|Fake_ClassLoader
name|classLoader
init|=
operator|new
name|Fake_ClassLoader
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setClassLoader
argument_list|(
name|classLoader
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|conf
operator|.
name|getClassLoader
argument_list|()
operator|instanceof
name|Fake_ClassLoader
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
name|testdir
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|Path
name|base
init|=
operator|new
name|Path
argument_list|(
name|testdir
argument_list|,
literal|"/empty"
argument_list|)
decl_stmt|;
name|Path
index|[]
name|src
init|=
block|{
operator|new
name|Path
argument_list|(
name|base
argument_list|,
literal|"i0"
argument_list|)
block|,
operator|new
name|Path
argument_list|(
literal|"i1"
argument_list|)
block|,
operator|new
name|Path
argument_list|(
literal|"i2"
argument_list|)
block|}
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CompositeInputFormat
operator|.
name|JOIN_EXPR
argument_list|,
name|CompositeInputFormat
operator|.
name|compose
argument_list|(
literal|"outer"
argument_list|,
name|IF_ClassLoaderChecker
operator|.
name|class
argument_list|,
name|src
argument_list|)
argument_list|)
expr_stmt|;
name|CompositeInputFormat
argument_list|<
name|NullWritable
argument_list|>
name|inputFormat
init|=
operator|new
name|CompositeInputFormat
argument_list|<
name|NullWritable
argument_list|>
argument_list|()
decl_stmt|;
comment|// create dummy TaskAttemptID
name|TaskAttemptID
name|tid
init|=
operator|new
name|TaskAttemptID
argument_list|(
literal|"jt"
argument_list|,
literal|1
argument_list|,
name|TaskType
operator|.
name|MAP
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|TASK_ATTEMPT_ID
argument_list|,
name|tid
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|inputFormat
operator|.
name|createRecordReader
argument_list|(
name|inputFormat
operator|.
name|getSplits
argument_list|(
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|TaskAttemptContextImpl
argument_list|(
name|conf
argument_list|,
name|tid
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|Fake_ClassLoader
specifier|public
specifier|static
class|class
name|Fake_ClassLoader
extends|extends
name|ClassLoader
block|{   }
DECL|class|IF_ClassLoaderChecker
specifier|public
specifier|static
class|class
name|IF_ClassLoaderChecker
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|MapReduceTestUtil
operator|.
name|Fake_IF
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|method|IF_ClassLoaderChecker ()
specifier|public
name|IF_ClassLoaderChecker
parameter_list|()
block|{     }
DECL|method|createRecordReader (InputSplit ignored, TaskAttemptContext context)
specifier|public
name|RecordReader
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|createRecordReader
parameter_list|(
name|InputSplit
name|ignored
parameter_list|,
name|TaskAttemptContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|RR_ClassLoaderChecker
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|context
operator|.
name|getConfiguration
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|class|RR_ClassLoaderChecker
specifier|public
specifier|static
class|class
name|RR_ClassLoaderChecker
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Fake_RR
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|RR_ClassLoaderChecker (Configuration conf)
specifier|public
name|RR_ClassLoaderChecker
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"The class loader has not been inherited from "
operator|+
name|CompositeRecordReader
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|conf
operator|.
name|getClassLoader
argument_list|()
operator|instanceof
name|Fake_ClassLoader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

