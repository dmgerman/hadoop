begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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

begin_class
DECL|class|DeskewedJobTraceReader
specifier|public
class|class
name|DeskewedJobTraceReader
implements|implements
name|Closeable
block|{
comment|// underlying engine
DECL|field|reader
specifier|private
specifier|final
name|JobTraceReader
name|reader
decl_stmt|;
comment|// configuration variables
DECL|field|skewBufferLength
specifier|private
specifier|final
name|int
name|skewBufferLength
decl_stmt|;
DECL|field|abortOnUnfixableSkew
specifier|private
specifier|final
name|boolean
name|abortOnUnfixableSkew
decl_stmt|;
comment|// state variables
DECL|field|skewMeasurementLatestSubmitTime
specifier|private
name|long
name|skewMeasurementLatestSubmitTime
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|returnedLatestSubmitTime
specifier|private
name|long
name|returnedLatestSubmitTime
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
DECL|field|maxSkewBufferNeeded
specifier|private
name|int
name|maxSkewBufferNeeded
init|=
literal|0
decl_stmt|;
comment|// a submit time will NOT be in countedRepeatedSubmitTimesSoFar if
comment|// it only occurs once. This situation is represented by having the
comment|// time in submitTimesSoFar only. A submit time that occurs twice or more
comment|// appears in countedRepeatedSubmitTimesSoFar [with the appropriate range
comment|// value] AND submitTimesSoFar
DECL|field|countedRepeatedSubmitTimesSoFar
specifier|private
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
name|countedRepeatedSubmitTimesSoFar
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|submitTimesSoFar
specifier|private
name|TreeSet
argument_list|<
name|Long
argument_list|>
name|submitTimesSoFar
init|=
operator|new
name|TreeSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|skewBuffer
specifier|private
specifier|final
name|PriorityQueue
argument_list|<
name|LoggedJob
argument_list|>
name|skewBuffer
decl_stmt|;
DECL|field|LOG
specifier|static
specifier|final
specifier|private
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DeskewedJobTraceReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|JobComparator
specifier|static
specifier|private
class|class
name|JobComparator
implements|implements
name|Comparator
argument_list|<
name|LoggedJob
argument_list|>
block|{
annotation|@
name|Override
DECL|method|compare (LoggedJob j1, LoggedJob j2)
specifier|public
name|int
name|compare
parameter_list|(
name|LoggedJob
name|j1
parameter_list|,
name|LoggedJob
name|j2
parameter_list|)
block|{
return|return
operator|(
name|j1
operator|.
name|getSubmitTime
argument_list|()
operator|<
name|j2
operator|.
name|getSubmitTime
argument_list|()
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
name|j1
operator|.
name|getSubmitTime
argument_list|()
operator|==
name|j2
operator|.
name|getSubmitTime
argument_list|()
operator|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
comment|/**    * Constructor.    *     * @param reader    *          the {@link JobTraceReader} that's being protected    * @param skewBufferLength    *          [the number of late jobs that can preced a later out-of-order    *          earlier job    * @throws IOException    */
DECL|method|DeskewedJobTraceReader (JobTraceReader reader, int skewBufferLength, boolean abortOnUnfixableSkew)
specifier|public
name|DeskewedJobTraceReader
parameter_list|(
name|JobTraceReader
name|reader
parameter_list|,
name|int
name|skewBufferLength
parameter_list|,
name|boolean
name|abortOnUnfixableSkew
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|skewBufferLength
operator|=
name|skewBufferLength
expr_stmt|;
name|this
operator|.
name|abortOnUnfixableSkew
operator|=
name|abortOnUnfixableSkew
expr_stmt|;
name|skewBuffer
operator|=
operator|new
name|PriorityQueue
argument_list|<
name|LoggedJob
argument_list|>
argument_list|(
name|skewBufferLength
operator|+
literal|1
argument_list|,
operator|new
name|JobComparator
argument_list|()
argument_list|)
expr_stmt|;
name|fillSkewBuffer
argument_list|()
expr_stmt|;
block|}
DECL|method|DeskewedJobTraceReader (JobTraceReader reader)
specifier|public
name|DeskewedJobTraceReader
parameter_list|(
name|JobTraceReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|rawNextJob ()
specifier|private
name|LoggedJob
name|rawNextJob
parameter_list|()
throws|throws
name|IOException
block|{
name|LoggedJob
name|result
init|=
name|reader
operator|.
name|getNext
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|abortOnUnfixableSkew
operator|||
name|skewBufferLength
operator|>
literal|0
operator|)
operator|&&
name|result
operator|!=
literal|null
condition|)
block|{
name|long
name|thisTime
init|=
name|result
operator|.
name|getSubmitTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|submitTimesSoFar
operator|.
name|contains
argument_list|(
name|thisTime
argument_list|)
condition|)
block|{
name|Integer
name|myCount
init|=
name|countedRepeatedSubmitTimesSoFar
operator|.
name|get
argument_list|(
name|thisTime
argument_list|)
decl_stmt|;
name|countedRepeatedSubmitTimesSoFar
operator|.
name|put
argument_list|(
name|thisTime
argument_list|,
name|myCount
operator|==
literal|null
condition|?
literal|2
else|:
name|myCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|submitTimesSoFar
operator|.
name|add
argument_list|(
name|thisTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|thisTime
operator|<
name|skewMeasurementLatestSubmitTime
condition|)
block|{
name|Iterator
argument_list|<
name|Long
argument_list|>
name|endCursor
init|=
name|submitTimesSoFar
operator|.
name|descendingIterator
argument_list|()
decl_stmt|;
name|int
name|thisJobNeedsSkew
init|=
literal|0
decl_stmt|;
name|Long
name|keyNeedingSkew
decl_stmt|;
while|while
condition|(
name|endCursor
operator|.
name|hasNext
argument_list|()
operator|&&
operator|(
name|keyNeedingSkew
operator|=
name|endCursor
operator|.
name|next
argument_list|()
operator|)
operator|>
name|thisTime
condition|)
block|{
name|Integer
name|keyNeedsSkewAmount
init|=
name|countedRepeatedSubmitTimesSoFar
operator|.
name|get
argument_list|(
name|keyNeedingSkew
argument_list|)
decl_stmt|;
name|thisJobNeedsSkew
operator|+=
name|keyNeedsSkewAmount
operator|==
literal|null
condition|?
literal|1
else|:
name|keyNeedsSkewAmount
expr_stmt|;
block|}
name|maxSkewBufferNeeded
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxSkewBufferNeeded
argument_list|,
name|thisJobNeedsSkew
argument_list|)
expr_stmt|;
block|}
name|skewMeasurementLatestSubmitTime
operator|=
name|Math
operator|.
name|max
argument_list|(
name|thisTime
argument_list|,
name|skewMeasurementLatestSubmitTime
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|class|OutOfOrderException
specifier|static
class|class
name|OutOfOrderException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|OutOfOrderException (String text)
specifier|public
name|OutOfOrderException
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nextJob ()
name|LoggedJob
name|nextJob
parameter_list|()
throws|throws
name|IOException
throws|,
name|OutOfOrderException
block|{
name|LoggedJob
name|newJob
init|=
name|rawNextJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|newJob
operator|!=
literal|null
condition|)
block|{
name|skewBuffer
operator|.
name|add
argument_list|(
name|newJob
argument_list|)
expr_stmt|;
block|}
name|LoggedJob
name|result
init|=
name|skewBuffer
operator|.
name|poll
argument_list|()
decl_stmt|;
while|while
condition|(
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getSubmitTime
argument_list|()
operator|<
name|returnedLatestSubmitTime
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The current job was submitted earlier than the previous one"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Its jobID is "
operator|+
name|result
operator|.
name|getJobID
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Its submit time is "
operator|+
name|result
operator|.
name|getSubmitTime
argument_list|()
operator|+
literal|",but the previous one was "
operator|+
name|returnedLatestSubmitTime
argument_list|)
expr_stmt|;
if|if
condition|(
name|abortOnUnfixableSkew
condition|)
block|{
throw|throw
operator|new
name|OutOfOrderException
argument_list|(
literal|"Job submit time is "
operator|+
name|result
operator|.
name|getSubmitTime
argument_list|()
operator|+
literal|",but the previous one was "
operator|+
name|returnedLatestSubmitTime
argument_list|)
throw|;
block|}
name|result
operator|=
name|rawNextJob
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|returnedLatestSubmitTime
operator|=
name|result
operator|.
name|getSubmitTime
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|fillSkewBuffer ()
specifier|private
name|void
name|fillSkewBuffer
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|skewBufferLength
condition|;
operator|++
name|i
control|)
block|{
name|LoggedJob
name|newJob
init|=
name|rawNextJob
argument_list|()
decl_stmt|;
if|if
condition|(
name|newJob
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|skewBuffer
operator|.
name|add
argument_list|(
name|newJob
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|neededSkewBufferSize ()
name|int
name|neededSkewBufferSize
parameter_list|()
block|{
return|return
name|maxSkewBufferNeeded
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

