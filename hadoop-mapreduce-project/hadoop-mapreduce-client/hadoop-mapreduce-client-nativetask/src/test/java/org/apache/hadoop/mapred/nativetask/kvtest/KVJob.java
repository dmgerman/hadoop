begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.kvtest
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|kvtest
package|;
end_package

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
name|zip
operator|.
name|CRC32
import|;
end_import

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
name|Stopwatch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
import|;
end_import

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
name|Log
import|;
end_import

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
name|mapred
operator|.
name|nativetask
operator|.
name|testutil
operator|.
name|BytesFactory
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
name|mapred
operator|.
name|nativetask
operator|.
name|testutil
operator|.
name|TestConstants
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
name|Job
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
name|Mapper
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
name|Reducer
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
name|lib
operator|.
name|input
operator|.
name|FileInputFormat
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
name|lib
operator|.
name|input
operator|.
name|SequenceFileInputFormat
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
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
import|;
end_import

begin_class
DECL|class|KVJob
specifier|public
class|class
name|KVJob
block|{
DECL|field|INPUTPATH
specifier|public
specifier|static
specifier|final
name|String
name|INPUTPATH
init|=
literal|"nativetask.kvtest.inputfile.path"
decl_stmt|;
DECL|field|OUTPUTPATH
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUTPATH
init|=
literal|"nativetask.kvtest.outputfile.path"
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KVJob
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|job
name|Job
name|job
init|=
literal|null
decl_stmt|;
DECL|class|ValueMapper
specifier|public
specifier|static
class|class
name|ValueMapper
parameter_list|<
name|KTYPE
parameter_list|,
name|VTYPE
parameter_list|>
extends|extends
name|Mapper
argument_list|<
name|KTYPE
argument_list|,
name|VTYPE
argument_list|,
name|KTYPE
argument_list|,
name|VTYPE
argument_list|>
block|{
annotation|@
name|Override
DECL|method|map (KTYPE key, VTYPE value, Context context)
specifier|public
name|void
name|map
parameter_list|(
name|KTYPE
name|key
parameter_list|,
name|VTYPE
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|KVMReducer
specifier|public
specifier|static
class|class
name|KVMReducer
parameter_list|<
name|KTYPE
parameter_list|,
name|VTYPE
parameter_list|>
extends|extends
name|Reducer
argument_list|<
name|KTYPE
argument_list|,
name|VTYPE
argument_list|,
name|KTYPE
argument_list|,
name|VTYPE
argument_list|>
block|{
DECL|method|reduce (KTYPE key, VTYPE value, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|KTYPE
name|key
parameter_list|,
name|VTYPE
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|KVReducer
specifier|public
specifier|static
class|class
name|KVReducer
parameter_list|<
name|KTYPE
parameter_list|,
name|VTYPE
parameter_list|>
extends|extends
name|Reducer
argument_list|<
name|KTYPE
argument_list|,
name|VTYPE
argument_list|,
name|KTYPE
argument_list|,
name|VTYPE
argument_list|>
block|{
annotation|@
name|Override
DECL|method|reduce (KTYPE key, Iterable<VTYPE> values, Context context)
specifier|public
name|void
name|reduce
parameter_list|(
name|KTYPE
name|key
parameter_list|,
name|Iterable
argument_list|<
name|VTYPE
argument_list|>
name|values
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|long
name|resultlong
init|=
literal|0
decl_stmt|;
comment|// 8 bytes match BytesFactory.fromBytes function
specifier|final
name|CRC32
name|crc32
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|VTYPE
name|val
range|:
name|values
control|)
block|{
name|crc32
operator|.
name|reset
argument_list|()
expr_stmt|;
name|crc32
operator|.
name|update
argument_list|(
name|BytesFactory
operator|.
name|toBytes
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
name|resultlong
operator|+=
name|crc32
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|VTYPE
name|V
init|=
literal|null
decl_stmt|;
name|context
operator|.
name|write
argument_list|(
name|key
argument_list|,
operator|(
name|VTYPE
operator|)
name|BytesFactory
operator|.
name|newObject
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|resultlong
argument_list|)
argument_list|,
name|V
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|KVJob (String jobname, Configuration conf, Class<?> keyclass, Class<?> valueclass, String inputpath, String outputpath)
specifier|public
name|KVJob
parameter_list|(
name|String
name|jobname
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|keyclass
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|valueclass
parameter_list|,
name|String
name|inputpath
parameter_list|,
name|String
name|outputpath
parameter_list|)
throws|throws
name|Exception
block|{
name|job
operator|=
operator|new
name|Job
argument_list|(
name|conf
argument_list|,
name|jobname
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|KVJob
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|KVJob
operator|.
name|ValueMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|keyclass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|valueclass
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVTEST_CREATEFILE
argument_list|)
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
block|{
specifier|final
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
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|inputpath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|TestInputFile
name|testfile
init|=
operator|new
name|TestInputFile
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|FILESIZE_KEY
argument_list|,
literal|"1000"
argument_list|)
argument_list|)
argument_list|,
name|keyclass
operator|.
name|getName
argument_list|()
argument_list|,
name|valueclass
operator|.
name|getName
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Stopwatch
name|sw
init|=
operator|new
name|Stopwatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|testfile
operator|.
name|createSequenceTestFile
argument_list|(
name|inputpath
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created test file "
operator|+
name|inputpath
operator|+
literal|" in "
operator|+
name|sw
operator|.
name|elapsedMillis
argument_list|()
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
block|}
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|inputpath
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|outputpath
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runJob ()
specifier|public
name|boolean
name|runJob
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
return|;
block|}
block|}
end_class

end_unit

