begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.jobhistory
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
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
name|mapreduce
operator|.
name|JobID
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
name|TypeConverter
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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

begin_class
DECL|class|TestFileNameIndexUtils
specifier|public
class|class
name|TestFileNameIndexUtils
block|{
DECL|field|OLD_JOB_HISTORY_FILE_FORMATTER
specifier|private
specifier|static
specifier|final
name|String
name|OLD_JOB_HISTORY_FILE_FORMATTER
init|=
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|JobHistoryUtils
operator|.
name|JOB_HISTORY_FILE_EXTENSION
decl_stmt|;
DECL|field|JOB_HISTORY_FILE_FORMATTER
specifier|private
specifier|static
specifier|final
name|String
name|JOB_HISTORY_FILE_FORMATTER
init|=
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"%s"
operator|+
name|JobHistoryUtils
operator|.
name|JOB_HISTORY_FILE_EXTENSION
decl_stmt|;
DECL|field|JOB_ID
specifier|private
specifier|static
specifier|final
name|String
name|JOB_ID
init|=
literal|"job_1317928501754_0001"
decl_stmt|;
DECL|field|SUBMIT_TIME
specifier|private
specifier|static
specifier|final
name|String
name|SUBMIT_TIME
init|=
literal|"1317928742025"
decl_stmt|;
DECL|field|USER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|USER_NAME
init|=
literal|"username"
decl_stmt|;
DECL|field|USER_NAME_WITH_DELIMITER
specifier|private
specifier|static
specifier|final
name|String
name|USER_NAME_WITH_DELIMITER
init|=
literal|"user"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"name"
decl_stmt|;
DECL|field|USER_NAME_WITH_DELIMITER_ESCAPE
specifier|private
specifier|static
specifier|final
name|String
name|USER_NAME_WITH_DELIMITER_ESCAPE
init|=
literal|"user"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER_ESCAPE
operator|+
literal|"name"
decl_stmt|;
DECL|field|JOB_NAME
specifier|private
specifier|static
specifier|final
name|String
name|JOB_NAME
init|=
literal|"mapreduce"
decl_stmt|;
DECL|field|JOB_NAME_WITH_DELIMITER
specifier|private
specifier|static
specifier|final
name|String
name|JOB_NAME_WITH_DELIMITER
init|=
literal|"map"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER
operator|+
literal|"reduce"
decl_stmt|;
DECL|field|JOB_NAME_WITH_DELIMITER_ESCAPE
specifier|private
specifier|static
specifier|final
name|String
name|JOB_NAME_WITH_DELIMITER_ESCAPE
init|=
literal|"map"
operator|+
name|FileNameIndexUtils
operator|.
name|DELIMITER_ESCAPE
operator|+
literal|"reduce"
decl_stmt|;
DECL|field|FINISH_TIME
specifier|private
specifier|static
specifier|final
name|String
name|FINISH_TIME
init|=
literal|"1317928754958"
decl_stmt|;
DECL|field|NUM_MAPS
specifier|private
specifier|static
specifier|final
name|String
name|NUM_MAPS
init|=
literal|"1"
decl_stmt|;
DECL|field|NUM_REDUCES
specifier|private
specifier|static
specifier|final
name|String
name|NUM_REDUCES
init|=
literal|"1"
decl_stmt|;
DECL|field|JOB_STATUS
specifier|private
specifier|static
specifier|final
name|String
name|JOB_STATUS
init|=
literal|"SUCCEEDED"
decl_stmt|;
DECL|field|QUEUE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_NAME
init|=
literal|"default"
decl_stmt|;
DECL|field|JOB_START_TIME
specifier|private
specifier|static
specifier|final
name|String
name|JOB_START_TIME
init|=
literal|"1317928742060"
decl_stmt|;
annotation|@
name|Test
DECL|method|testEncodingDecodingEquivalence ()
specifier|public
name|void
name|testEncodingDecodingEquivalence
parameter_list|()
throws|throws
name|IOException
block|{
name|JobIndexInfo
name|info
init|=
operator|new
name|JobIndexInfo
argument_list|()
decl_stmt|;
name|JobID
name|oldJobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobId
argument_list|)
decl_stmt|;
name|info
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubmitTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|SUBMIT_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUser
argument_list|(
name|USER_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobName
argument_list|(
name|JOB_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setFinishTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|FINISH_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumMaps
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_MAPS
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumReduces
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_REDUCES
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStatus
argument_list|(
name|JOB_STATUS
argument_list|)
expr_stmt|;
name|info
operator|.
name|setQueueName
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStartTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|JOB_START_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jobHistoryFile
init|=
name|FileNameIndexUtils
operator|.
name|getDoneFileName
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|JobIndexInfo
name|parsedInfo
init|=
name|FileNameIndexUtils
operator|.
name|getIndexInfo
argument_list|(
name|jobHistoryFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job id different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getJobId
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Submit time different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"User different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getUser
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job name different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getJobName
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Finish time different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num maps different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getNumMaps
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getNumMaps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num reduces different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getNumReduces
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getNumReduces
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job status different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getJobStatus
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getJobStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Queue name different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getQueueName
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job start time different after encoding and decoding"
argument_list|,
name|info
operator|.
name|getJobStartTime
argument_list|()
argument_list|,
name|parsedInfo
operator|.
name|getJobStartTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserNamePercentEncoding ()
specifier|public
name|void
name|testUserNamePercentEncoding
parameter_list|()
throws|throws
name|IOException
block|{
name|JobIndexInfo
name|info
init|=
operator|new
name|JobIndexInfo
argument_list|()
decl_stmt|;
name|JobID
name|oldJobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobId
argument_list|)
decl_stmt|;
name|info
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubmitTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|SUBMIT_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUser
argument_list|(
name|USER_NAME_WITH_DELIMITER
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobName
argument_list|(
name|JOB_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setFinishTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|FINISH_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumMaps
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_MAPS
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumReduces
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_REDUCES
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStatus
argument_list|(
name|JOB_STATUS
argument_list|)
expr_stmt|;
name|info
operator|.
name|setQueueName
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStartTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|JOB_START_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jobHistoryFile
init|=
name|FileNameIndexUtils
operator|.
name|getDoneFileName
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"User name not encoded correctly into job history file"
argument_list|,
name|jobHistoryFile
operator|.
name|contains
argument_list|(
name|USER_NAME_WITH_DELIMITER_ESCAPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUserNamePercentDecoding ()
specifier|public
name|void
name|testUserNamePercentDecoding
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jobHistoryFile
init|=
name|String
operator|.
name|format
argument_list|(
name|JOB_HISTORY_FILE_FORMATTER
argument_list|,
name|JOB_ID
argument_list|,
name|SUBMIT_TIME
argument_list|,
name|USER_NAME_WITH_DELIMITER_ESCAPE
argument_list|,
name|JOB_NAME
argument_list|,
name|FINISH_TIME
argument_list|,
name|NUM_MAPS
argument_list|,
name|NUM_REDUCES
argument_list|,
name|JOB_STATUS
argument_list|,
name|QUEUE_NAME
argument_list|,
name|JOB_START_TIME
argument_list|)
decl_stmt|;
name|JobIndexInfo
name|info
init|=
name|FileNameIndexUtils
operator|.
name|getIndexInfo
argument_list|(
name|jobHistoryFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"User name doesn't match"
argument_list|,
name|USER_NAME_WITH_DELIMITER
argument_list|,
name|info
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobNamePercentEncoding ()
specifier|public
name|void
name|testJobNamePercentEncoding
parameter_list|()
throws|throws
name|IOException
block|{
name|JobIndexInfo
name|info
init|=
operator|new
name|JobIndexInfo
argument_list|()
decl_stmt|;
name|JobID
name|oldJobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobId
argument_list|)
decl_stmt|;
name|info
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubmitTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|SUBMIT_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setUser
argument_list|(
name|USER_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobName
argument_list|(
name|JOB_NAME_WITH_DELIMITER
argument_list|)
expr_stmt|;
name|info
operator|.
name|setFinishTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|FINISH_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumMaps
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_MAPS
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setNumReduces
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_REDUCES
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStatus
argument_list|(
name|JOB_STATUS
argument_list|)
expr_stmt|;
name|info
operator|.
name|setQueueName
argument_list|(
name|QUEUE_NAME
argument_list|)
expr_stmt|;
name|info
operator|.
name|setJobStartTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|JOB_START_TIME
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jobHistoryFile
init|=
name|FileNameIndexUtils
operator|.
name|getDoneFileName
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Job name not encoded correctly into job history file"
argument_list|,
name|jobHistoryFile
operator|.
name|contains
argument_list|(
name|JOB_NAME_WITH_DELIMITER_ESCAPE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobNamePercentDecoding ()
specifier|public
name|void
name|testJobNamePercentDecoding
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|jobHistoryFile
init|=
name|String
operator|.
name|format
argument_list|(
name|JOB_HISTORY_FILE_FORMATTER
argument_list|,
name|JOB_ID
argument_list|,
name|SUBMIT_TIME
argument_list|,
name|USER_NAME
argument_list|,
name|JOB_NAME_WITH_DELIMITER_ESCAPE
argument_list|,
name|FINISH_TIME
argument_list|,
name|NUM_MAPS
argument_list|,
name|NUM_REDUCES
argument_list|,
name|JOB_STATUS
argument_list|,
name|QUEUE_NAME
argument_list|,
name|JOB_START_TIME
argument_list|)
decl_stmt|;
name|JobIndexInfo
name|info
init|=
name|FileNameIndexUtils
operator|.
name|getIndexInfo
argument_list|(
name|jobHistoryFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job name doesn't match"
argument_list|,
name|JOB_NAME_WITH_DELIMITER
argument_list|,
name|info
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testJobHistoryFileNameBackwardsCompatible ()
specifier|public
name|void
name|testJobHistoryFileNameBackwardsCompatible
parameter_list|()
throws|throws
name|IOException
block|{
name|JobID
name|oldJobId
init|=
name|JobID
operator|.
name|forName
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobId
argument_list|)
decl_stmt|;
name|long
name|submitTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|SUBMIT_TIME
argument_list|)
decl_stmt|;
name|long
name|finishTime
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|FINISH_TIME
argument_list|)
decl_stmt|;
name|int
name|numMaps
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_MAPS
argument_list|)
decl_stmt|;
name|int
name|numReduces
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|NUM_REDUCES
argument_list|)
decl_stmt|;
name|String
name|jobHistoryFile
init|=
name|String
operator|.
name|format
argument_list|(
name|OLD_JOB_HISTORY_FILE_FORMATTER
argument_list|,
name|JOB_ID
argument_list|,
name|SUBMIT_TIME
argument_list|,
name|USER_NAME
argument_list|,
name|JOB_NAME
argument_list|,
name|FINISH_TIME
argument_list|,
name|NUM_MAPS
argument_list|,
name|NUM_REDUCES
argument_list|,
name|JOB_STATUS
argument_list|)
decl_stmt|;
name|JobIndexInfo
name|info
init|=
name|FileNameIndexUtils
operator|.
name|getIndexInfo
argument_list|(
name|jobHistoryFile
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job id incorrect after decoding old history file"
argument_list|,
name|jobId
argument_list|,
name|info
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Submit time incorrect after decoding old history file"
argument_list|,
name|submitTime
argument_list|,
name|info
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"User incorrect after decoding old history file"
argument_list|,
name|USER_NAME
argument_list|,
name|info
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job name incorrect after decoding old history file"
argument_list|,
name|JOB_NAME
argument_list|,
name|info
operator|.
name|getJobName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Finish time incorrect after decoding old history file"
argument_list|,
name|finishTime
argument_list|,
name|info
operator|.
name|getFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num maps incorrect after decoding old history file"
argument_list|,
name|numMaps
argument_list|,
name|info
operator|.
name|getNumMaps
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Num reduces incorrect after decoding old history file"
argument_list|,
name|numReduces
argument_list|,
name|info
operator|.
name|getNumReduces
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job status incorrect after decoding old history file"
argument_list|,
name|JOB_STATUS
argument_list|,
name|info
operator|.
name|getJobStatus
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
literal|"Queue name incorrect after decoding old history file"
argument_list|,
name|info
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

