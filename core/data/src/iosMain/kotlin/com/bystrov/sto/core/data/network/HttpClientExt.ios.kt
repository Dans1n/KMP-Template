package com.bystrov.sto.core.data.network

import com.bystrov.sto.core.domain.util.DataError
import com.bystrov.sto.core.domain.util.Result
import io.ktor.client.engine.darwin.DarwinHttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.SerializationException
import platform.Foundation.NSURLErrorCallIsActive
import platform.Foundation.NSURLErrorCannotFindHost
import platform.Foundation.NSURLErrorDNSLookupFailed
import platform.Foundation.NSURLErrorDataNotAllowed
import platform.Foundation.NSURLErrorDomain
import platform.Foundation.NSURLErrorInternationalRoamingOff
import platform.Foundation.NSURLErrorNetworkConnectionLost
import platform.Foundation.NSURLErrorNotConnectedToInternet
import platform.Foundation.NSURLErrorResourceUnavailable
import platform.Foundation.NSURLErrorTimedOut
import kotlin.coroutines.coroutineContext

actual suspend fun <T> platformSafeCall(
    execute: suspend () -> HttpResponse,
    handleResponse: suspend (HttpResponse) -> Result<T, DataError.Remote>
): Result<T, DataError.Remote> {
    return try {
        val response = execute()
        handleResponse(response)
    } catch (e: DarwinHttpRequestException) {
        handleDarwinException(e)
    } catch (e: UnresolvedAddressException) {
        Result.Failure(DataError.Remote.NO_INTERNET)
    } catch (e: HttpRequestTimeoutException) {
        Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
    } catch (e: SerializationException) {
        Result.Failure(DataError.Remote.SERIALIZATION)
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        Result.Failure(DataError.Remote.UNKNOWN)
    }
}

private fun <T> handleDarwinException(e: DarwinHttpRequestException): Result<T, DataError.Remote> {
    val nsError = e.origin
    return if (nsError.domain == NSURLErrorDomain) {
        when (nsError.code) {
            NSURLErrorNotConnectedToInternet.toLong(),
            NSURLErrorNetworkConnectionLost.toLong(),
            NSURLErrorCannotFindHost.toLong(),
            NSURLErrorDNSLookupFailed.toLong(),
            NSURLErrorResourceUnavailable.toLong(),
            NSURLErrorInternationalRoamingOff.toLong(),
            NSURLErrorCallIsActive.toLong(),
            NSURLErrorDataNotAllowed.toLong() -> {
                Result.Failure(DataError.Remote.NO_INTERNET)
            }
            NSURLErrorTimedOut.toLong() -> Result.Failure(DataError.Remote.REQUEST_TIMEOUT)
            else -> Result.Failure(DataError.Remote.UNKNOWN)
        }
    } else Result.Failure(DataError.Remote.UNKNOWN)
}
