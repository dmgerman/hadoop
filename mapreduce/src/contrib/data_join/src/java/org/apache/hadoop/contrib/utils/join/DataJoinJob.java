begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.utils.join
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|utils
operator|.
name|join
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
name|SequenceFile
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
name|Text
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
name|FileOutputFormat
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
name|JobClient
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
name|JobConf
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
name|mapred
operator|.
name|RunningJob
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
name|mapred
operator|.
name|SequenceFileOutputFormat
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
name|TextInputFormat
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
name|TextOutputFormat
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
name|JobID
import|;
end_import

begin_comment
comment|/**  * This class implements the main function for creating a map/reduce  * job to join data of different sources. To create sucn a job, the   * user must implement a mapper class that extends DataJoinMapperBase class,  * and a reducer class that extends DataJoinReducerBase.   *   */
end_comment

begin_class
DECL|class|DataJoinJob
specifier|public
class|class
name|DataJoinJob
block|{
DECL|method|getClassByName (String className)
specifier|public
specifier|static
name|Class
name|getClassByName
parameter_list|(
name|String
name|className
parameter_list|)
block|{
name|Class
name|retv
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ClassLoader
name|classLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|retv
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|,
literal|true
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|retv
return|;
block|}
DECL|method|createDataJoinJob (String args[])
specifier|public
specifier|static
name|JobConf
name|createDataJoinJob
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|inputDir
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|String
name|outputDir
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|Class
name|inputFormat
init|=
name|SequenceFileInputFormat
operator|.
name|class
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"text"
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using SequenceFileInputFormat: "
operator|+
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using TextInputFormat: "
operator|+
name|args
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|inputFormat
operator|=
name|TextInputFormat
operator|.
name|class
expr_stmt|;
block|}
name|int
name|numOfReducers
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|3
index|]
argument_list|)
decl_stmt|;
name|Class
name|mapper
init|=
name|getClassByName
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|Class
name|reducer
init|=
name|getClassByName
argument_list|(
name|args
index|[
literal|5
index|]
argument_list|)
decl_stmt|;
name|Class
name|mapoutputValueClass
init|=
name|getClassByName
argument_list|(
name|args
index|[
literal|6
index|]
argument_list|)
decl_stmt|;
name|Class
name|outputFormat
init|=
name|TextOutputFormat
operator|.
name|class
decl_stmt|;
name|Class
name|outputValueClass
init|=
name|Text
operator|.
name|class
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|7
index|]
operator|.
name|compareToIgnoreCase
argument_list|(
literal|"text"
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using SequenceFileOutputFormat: "
operator|+
name|args
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
name|outputFormat
operator|=
name|SequenceFileOutputFormat
operator|.
name|class
expr_stmt|;
name|outputValueClass
operator|=
name|getClassByName
argument_list|(
name|args
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Using TextOutputFormat: "
operator|+
name|args
index|[
literal|7
index|]
argument_list|)
expr_stmt|;
block|}
name|long
name|maxNumOfValuesPerGroup
init|=
literal|100
decl_stmt|;
name|String
name|jobName
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|8
condition|)
block|{
name|maxNumOfValuesPerGroup
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|args
index|[
literal|8
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|9
condition|)
block|{
name|jobName
operator|=
name|args
index|[
literal|9
index|]
expr_stmt|;
block|}
name|Configuration
name|defaults
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|JobConf
name|job
init|=
operator|new
name|JobConf
argument_list|(
name|defaults
argument_list|,
name|DataJoinJob
operator|.
name|class
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJobName
argument_list|(
literal|"DataJoinJob: "
operator|+
name|jobName
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|defaults
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|outputDir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|job
argument_list|,
name|inputDir
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormat
argument_list|(
name|inputFormat
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|mapper
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
name|outputDir
argument_list|)
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputFormat
argument_list|(
name|outputFormat
argument_list|)
expr_stmt|;
name|SequenceFileOutputFormat
operator|.
name|setOutputCompressionType
argument_list|(
name|job
argument_list|,
name|SequenceFile
operator|.
name|CompressionType
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapOutputValueClass
argument_list|(
name|mapoutputValueClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|outputValueClass
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|reducer
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumMapTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|job
operator|.
name|setNumReduceTasks
argument_list|(
name|numOfReducers
argument_list|)
expr_stmt|;
name|job
operator|.
name|setLong
argument_list|(
literal|"datajoin.maxNumOfValuesPerGroup"
argument_list|,
name|maxNumOfValuesPerGroup
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
comment|/**    * Submit/run a map/reduce job.    *     * @param job    * @return true for success    * @throws IOException    */
DECL|method|runJob (JobConf job)
specifier|public
specifier|static
name|boolean
name|runJob
parameter_list|(
name|JobConf
name|job
parameter_list|)
throws|throws
name|IOException
block|{
name|JobClient
name|jc
init|=
operator|new
name|JobClient
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|boolean
name|sucess
init|=
literal|true
decl_stmt|;
name|RunningJob
name|running
init|=
literal|null
decl_stmt|;
try|try
block|{
name|running
operator|=
name|jc
operator|.
name|submitJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|JobID
name|jobId
init|=
name|running
operator|.
name|getID
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job "
operator|+
name|jobId
operator|+
literal|" is submitted"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|running
operator|.
name|isComplete
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job "
operator|+
name|jobId
operator|+
literal|" is still running."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
name|running
operator|=
name|jc
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
block|}
name|sucess
operator|=
name|running
operator|.
name|isSuccessful
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|sucess
operator|&&
operator|(
name|running
operator|!=
literal|null
operator|)
condition|)
block|{
name|running
operator|.
name|killJob
argument_list|()
expr_stmt|;
block|}
name|jc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|sucess
return|;
block|}
comment|/**    * @param args    */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|boolean
name|success
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
argument_list|<
literal|8
operator|||
name|args
operator|.
name|length
argument_list|>
literal|10
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"usage: DataJoinJob "
operator|+
literal|"inputdirs outputdir map_input_file_format "
operator|+
literal|"numofParts "
operator|+
literal|"mapper_class "
operator|+
literal|"reducer_class "
operator|+
literal|"map_output_value_class "
operator|+
literal|"output_value_class [maxNumOfValuesPerGroup [descriptionOfJob]]]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|JobConf
name|job
init|=
name|DataJoinJob
operator|.
name|createDataJoinJob
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|success
operator|=
name|DataJoinJob
operator|.
name|runJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Job failed"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

