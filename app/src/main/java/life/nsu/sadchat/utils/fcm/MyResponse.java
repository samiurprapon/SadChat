package life.nsu.sadchat.utils.fcm;

import java.util.Arrays;

public class MyResponse {

    private int success;
    private int failure;
    private String multicast_id;
    private int canonical_ids;
    private Results[] results;

    public int getSuccess() {
        return success;
    }

    public int getFailure() {
        return failure;
    }

    public String getMulticast_id() {
        return multicast_id;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public Results[] getResults() {
        return results;
    }

    private class Results {
        String message_id;

        public String getMessage_id() {
            return message_id;
        }

        @Override
        public String toString() {
            return "Results{" +
                    "message_id='" + message_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MyResponse{" +
                "success=" + success +
                ", failure=" + failure +
                ", multicast_id='" + multicast_id + '\'' +
                ", canonical_ids=" + canonical_ids +
                ", results=" + Arrays.toString(results) +
                '}';
    }
}
